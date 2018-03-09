package com.tbj.comsumer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.tbj.bean.Credit1;
import com.tbj.bean.Credit1Log;
import com.tbj.bean.RepayLog;
import com.tbj.enums.HashTypeEnums;
import com.tbj.service.Credit1LogService;
import com.tbj.service.Credit1Service;

@Component("repayConsumer")
public class RepayConsumer implements ChannelAwareMessageListener{
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private Credit1Service credit1Service;
	
	@Autowired
	private Credit1LogService credit1LogService;

	public void onMessage(Message message, Channel channel) throws Exception {
		
		System.out.println("消息消费端");
		
		try {
			String key = new String(message.getBody());
			if(!redisTemplate.opsForHash().hasKey(HashTypeEnums.SUC_SEND_DATA.getVal(), key)){
				//表示在redis中没有此数据
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
				System.out.println("no data");
				return;
			}
			
			//②：在查看此条数据是否真实
			//模拟调用 accountLog 根据 key 查询, 如果accountLog中存在此数据,那么继续，如果不存在 ,直接 ack 回执.
			
			//③：先做幂等性判断：
			//再看自己的日志记录表,creditLog,查询此条数据之后是否消费过,如果存在,那么直接 ack 返回.
			System.out.println(credit1LogService.countByKey(key) + "---------------");
			if(credit1LogService.countByKey(key) != 0){
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
				System.out.println("repeat data");
				return;
			}
			
			
			//如果数据同时存在 pre suc中，那么删除pre中的数据
			if(redisTemplate.opsForHash().hasKey(HashTypeEnums.PRE_DATA.getVal(), key) && redisTemplate.opsForHash().hasKey(HashTypeEnums.SUC_SEND_DATA.getVal(), key)){
				//删除 pre 中的数据
				redisTemplate.opsForHash().delete(HashTypeEnums.PRE_DATA.getVal(), key);
			}
			
			//对于在redis中的数据，如果重试次数大于指定次数，那么将此数据移入到 死亡组，然后等待分析是否需要复活.
			String json = (String) redisTemplate.opsForHash().get(HashTypeEnums.SUC_SEND_DATA.getVal(), key);
			RepayLog model = JSON.parseObject(json, RepayLog.class);
			//超过重试5次就进入死忙队列
			if(model.getNum() > 5){
				//将此记录移入死亡队列
				Map<String, String> map = new HashMap<String, String>();
				map.put(key, JSON.toJSONString(model));
				redisTemplate.opsForHash().putAll(HashTypeEnums.DEATH_DATA.getVal(), map);
				redisTemplate.opsForHash().delete(HashTypeEnums.SUC_SEND_DATA.getVal(), key);
				//掉ack回执.
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
				System.out.println("inter death");
				return;
			}
			
			//将消息中的次数+1
			model.setNum(model.getNum() + 1);
			Map<String, String> map = new HashMap<String, String>();
			map.put(key, JSON.toJSONString(model));
			redisTemplate.opsForHash().putAll(HashTypeEnums.SUC_SEND_DATA.getVal(), map);
			
			//处理业务逻辑
			Credit1 credit1 = new Credit1();
			credit1.setCompanyId(model.getCompanyId());
			credit1.setCreditLine(model.getMoney());
			int update = credit1Service.update(credit1);
			
			Credit1Log credit1Log = new Credit1Log();
			credit1Log.setKey(key);
			credit1LogService.insert(credit1Log);
			System.out.println(update);
		} catch (Exception e1) {
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
			//抛出异常
			throw new RuntimeException("还款处理失败");
		}
		
		//当去全部操作成功,那么进行消息回执
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		
		System.out.println("success");
		
		
		
//		System.out.println("credit...");
//		System.out.println(new String(message.getBody()));
//		
//		String str = (String) redisTemplate.opsForHash().get("repay", new String(message.getBody()));
//		if(StringUtils.isNullOrEmpty(str)){
//			//说明redis中没有此条数据记录.
//			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//		}
//		System.out.println(str);
//		Log log = JSON.parseObject(str, Log.class);
//		if(log != null && "dealed".equalsIgnoreCase(log.getStatus())){
//			//说明此条还款已经被处理
//			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//		}
//		System.out.println(log);
//		
//		//处理业务逻辑
//		try {
//			Credit1 model = new Credit1();
//			model.setCompanyId(log.getCompanyId());
//			model.setCreditLine(log.getMoney());
//			credit1Service.update(model);
//			//修改log状态
//			log.setStatus("awaitdeal");
//			String json = JSON.toJSONString(log);
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put(str, json);
//			redisTemplate.opsForHash().putAll(str, map);
//		} catch (Exception e) {
//			//业务逻辑处理失败;
//			//拒绝处理该消息.发送邮件到项目负责人.让其尽快处理
//			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
//			//抛出异常
//			throw new RuntimeException("还款处理失败");
//		}
//		
//		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}

}
