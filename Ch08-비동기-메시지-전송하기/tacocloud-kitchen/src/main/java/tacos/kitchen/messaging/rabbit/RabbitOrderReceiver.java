package tacos.kitchen.messaging.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import tacos.Order;
import tacos.kitchen.OrderReceiver;

@Profile("rabbitmq-template")
@Component("templateOrderReceiver")
public class RabbitOrderReceiver implements OrderReceiver {

  private RabbitTemplate rabbit;

  public RabbitOrderReceiver(RabbitTemplate rabbit) {
    this.rabbit = rabbit;
  }
  
  public Order receiveOrder() {
    /*
    //만약 사용 환경에 따라서 지연을 목표로 할 경우에는 이런 코드를 사용한다
    Message message = rabbit.recive("tacocloud.order.queue",30000);
    return message != null ? (Order) converter.fromMessage(message) : null;
     */
    return (Order) rabbit.receiveAndConvert("tacocloud.order.queue");

    /*
    //위 방법보다 더 간단한 방법은 아래와 같다
    //이 방법은 PatameterizedTypeReference를 직접 인자로 전달하여 Order 객체를 수신하게 하는 것이다
    return rabbit.receiveAndConvert("tacocloud.order.queue", new PatameterizedTypeReference<Order>() {});
     */
  }
  
}
