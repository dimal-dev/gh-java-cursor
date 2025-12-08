# Sub-Stage 7.4: Kafka Integration

## Goal
Implement Kafka producers and consumers for event-driven notifications.

---

## Kafka Configuration

```java
@Configuration
@EnableKafka
public class KafkaConfig {
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }
    
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.goodhelp.*");
        return new DefaultKafkaConsumerFactory<>(config);
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
```

---

## Topics

| Topic | Purpose | Producer | Consumer |
|-------|---------|----------|----------|
| `consultation-events` | Booking/cancellation events | Booking module | Notification |
| `payment-events` | Payment completion | Billing module | Notification |

---

## Event Publisher

```java
@Service
@Slf4j
public class KafkaEventPublisher implements DomainEventPublisher {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationEventPublisher localPublisher;
    
    @Override
    public void publish(DomainEvent event) {
        // Local (same JVM) handling
        localPublisher.publishEvent(event);
        
        // Kafka publishing for distributed/async handling
        String topic = getTopicForEvent(event);
        kafkaTemplate.send(topic, event.getClass().getSimpleName(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish event to Kafka: {}", event, ex);
                } else {
                    log.debug("Event published to {}: {}", topic, event);
                }
            });
    }
    
    private String getTopicForEvent(DomainEvent event) {
        return switch (event) {
            case ConsultationBookedEvent e -> "consultation-events";
            case ConsultationCancelledEvent e -> "consultation-events";
            case PaymentCompletedEvent e -> "payment-events";
            default -> "domain-events";
        };
    }
}
```

---

## Event Consumer

```java
@Component
@Slf4j
public class NotificationEventConsumer {
    
    private final TelegramService telegramService;
    private final EmailService emailService;
    private final TherapistSettingsRepository settingsRepository;
    private final TelegramNotificationFormatter formatter;
    
    @KafkaListener(topics = "consultation-events", groupId = "notification-service")
    public void handleConsultationEvent(ConsumerRecord<String, Object> record) {
        log.info("Received consultation event: {}", record.key());
        
        Object event = record.value();
        
        if (event instanceof ConsultationBookedEvent e) {
            notifyTherapistNewBooking(e);
        } else if (event instanceof ConsultationCancelledEvent e) {
            notifyTherapistCancellation(e);
        }
    }
    
    @KafkaListener(topics = "payment-events", groupId = "notification-service")
    public void handlePaymentEvent(ConsumerRecord<String, Object> record) {
        if (record.value() instanceof PaymentCompletedEvent e) {
            sendBookingConfirmationEmail(e);
        }
    }
    
    private void notifyTherapistNewBooking(ConsultationBookedEvent event) {
        settingsRepository.findByTherapistId(event.therapistId())
            .filter(s -> s.getTelegramChatId() != null)
            .ifPresent(settings -> {
                String message = formatter.formatNewBooking(event.toInfo(), Locale.UK);
                telegramService.sendMessage(settings.getTelegramChatId(), message);
            });
    }
}
```

---

## Configuration

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      auto-offset-reset: earliest
      enable-auto-commit: true
    producer:
      retries: 3
```

---

## Verification
- [ ] Events published to Kafka
- [ ] Consumers receive events
- [ ] Notifications sent on events
- [ ] Error handling works

---

## Next
Proceed to **7.5: Scheduled Notifications**

