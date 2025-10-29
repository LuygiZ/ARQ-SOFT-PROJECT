package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
//import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.mongomapper.PhotoMongoEntityMapper;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;

@Profile("mongodb")
@Primary
@Repository
@RequiredArgsConstructor
public class PhotoMongoRepositoryImpl implements PhotoRepository {

    private final SpringDataPhotoMongoRepository photoRepo;
    // private final PhotoMongoEntityMapper photoMapper;

    @Override
    public void deleteByPhotoFile(String photoFile) {
        photoRepo.deleteByPhotoFile(photoFile);
    }
}