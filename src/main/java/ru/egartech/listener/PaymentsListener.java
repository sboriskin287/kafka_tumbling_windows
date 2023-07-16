package ru.egartech.listener;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.egartech.dto.Payment;
import ru.egartech.dto.TumblingWindow;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Spliterator;

@EnableKafka
@Component
@RequiredArgsConstructor
public class PaymentsListener {
    private final KafkaTemplate<String, TumblingWindow> kafkaTemplate;

    @KafkaListener(topics = "payments")
    @Transactional
    public void listen(List<ConsumerRecord<String, Payment>> records, Acknowledgment ack) {
        Spliterator<ConsumerRecord<String, Payment>> prevSpliterator = null;
        Spliterator<ConsumerRecord<String, Payment>> currSpliterator = new ByMinuteSpliterator(records);
        var handledRecords = 0;
        while (currSpliterator != null) {
            if (prevSpliterator != null) {
                handledRecords += (int) prevSpliterator.estimateSize();
                TumblingWindow tw = new TumblingWindow();
                prevSpliterator.forEachRemaining(r -> {
                    tw.setSum(tw.getSum() + r.value().getPrice());
                    tw.getPaymentInstants().add(LocalDateTime.ofInstant(Instant.ofEpochMilli(r.timestamp()), ZoneId.systemDefault()));
                });
                kafkaTemplate.send("tumbling-window", tw);
            }
            prevSpliterator = currSpliterator;
            currSpliterator = currSpliterator.trySplit();
        }
        ack.nack(handledRecords, Duration.ofMillis(0));
    }
}

