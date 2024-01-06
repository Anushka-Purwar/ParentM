package com.anushka.orderservice.service;

import brave.Tracer;
import com.anushka.orderservice.dto.InventoryResponse;
import com.anushka.orderservice.dto.OrderLineItemsDTO;
import com.anushka.orderservice.dto.OrderRequest;
import com.anushka.orderservice.event.OrderPlacedEvent;
import com.anushka.orderservice.model.Order;
import com.anushka.orderservice.model.OrderLineItems;
import com.anushka.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;
    public String placeOrder(OrderRequest orderRequest){
        Order order=new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        //we can create our own span id through tracer.nextspan().name("")
        //we need to assign the entire code in try finally block

        //call inventory to check whether order is in stock or not
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory"
                ,uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)//to be able to read from inventory
                .block();//client will automatically make an asyn request this code will convert it to syn
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
        //this code will convert the array in stream and all match method will check is all products are in stock
        //or not even if one of items is in stock return false
        if(allProductsInStock) {
            orderRepository.save(order);
            //kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            return "Order placed sucessfully";
        }
        else {
            throw new IllegalArgumentException("Product is not in stock, Please try again");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDTO orderLineItemsDTO) {
        OrderLineItems orderLineItems=new OrderLineItems();
        orderLineItems.setPrice(orderLineItems.getPrice());
        orderLineItems.setQuantity(orderLineItems.getQuantity());
        orderLineItems.setSkuCode(orderLineItems.getSkuCode());
        return orderLineItems;
    }
}
