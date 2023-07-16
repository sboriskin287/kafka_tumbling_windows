package ru.egartech.listener;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.util.CollectionUtils;
import ru.egartech.dto.Payment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@AllArgsConstructor
public class ByMinuteSpliterator implements Spliterator<ConsumerRecord<String, Payment>> {
    private List<ConsumerRecord<String, Payment>> records;
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public boolean tryAdvance(Consumer<? super ConsumerRecord<String, Payment>> action) {
        if (index.get() < records.size()) {
            action.accept(records.get(index.getAndIncrement()));
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<ConsumerRecord<String, Payment>> trySplit() {
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        LocalDateTime firstLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(records.get(0).timestamp()), ZoneId.systemDefault());
        for (int i = 1; i < records.size(); i += 1) {
            var currRec = records.get(i);
            var currLdt = LocalDateTime.ofInstant(Instant.ofEpochMilli(currRec.timestamp()), ZoneId.systemDefault());
            if (Objects.hash(firstLdt.getYear(), firstLdt.getMonth(), firstLdt.getDayOfMonth(), firstLdt.getHour(), firstLdt.getMinute()) !=
                    Objects.hash(currLdt.getYear(), currLdt.getMonth(), currLdt.getDayOfMonth(), currLdt.getHour(), currLdt.getMinute())) {
                var subList = records.subList(i, records.size());
                records = records.subList(0, i);
                return new ByMinuteSpliterator(subList);
            }
        }
        return null;
    }

    @Override
    public long estimateSize() {
        return Integer.max(0, records.size() - index.get());
    }

    @Override
    public int characteristics() {
        return SIZED | SUBSIZED;
    }
}
