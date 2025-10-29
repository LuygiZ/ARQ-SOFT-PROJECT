package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb.mongomapper.FineMongoEntityMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.FineMongoEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.FineRepository;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Profile("mongodb")
@Primary
@Repository
@RequiredArgsConstructor
public class FineMongoRepositoryImpl implements FineRepository {

    private final SpringDataFineMongoRepository fineRepo;
    private final FineMongoEntityMapper fineMapper;

    @Override
    public Optional<Fine> findByLendingNumber(String lendingNumber) {
        return fineRepo.findByLending_LendingNumber_LendingNumber(lendingNumber)
                .map(fineMapper::toModel);
    }

    @Override
    public Iterable<Fine> findAll() {
        return StreamSupport.stream(fineRepo.findAll().spliterator(), false)
                .map(fineMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Fine save(Fine fine) {
        FineMongoEntity entity = fineMapper.toMongoEntity(fine);
        FineMongoEntity saved = fineRepo.save(entity);
        return fineMapper.toModel(saved);
    }
}