package com.zeki.demo.exerciselibrary.mapperentitydomain;

import com.zeki.demo.exerciselibrary.domain.ExerciseImage;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibrary;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibraryItem;
import com.zeki.demo.exerciselibrary.domain.ExerciseLicense;
import com.zeki.demo.exerciselibrary.domain.ExerciseMuscle;
import com.zeki.demo.exerciselibrary.domain.ExerciseReference;
import com.zeki.demo.exerciselibrary.domain.ExerciseVideo;
import com.zeki.demo.exerciselibrary.entity.ExerciseImageEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseLibraryEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseLibraryItemEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseLicenseEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseMuscleEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseReferenceEntity;
import com.zeki.demo.exerciselibrary.entity.ExerciseVideoEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ExerciseLibraryEntityMapper {

    public ExerciseLibrary toDomain(ExerciseLibraryEntity entity) {
        return new ExerciseLibrary(
                entity.results().stream().map(this::toDomain).toList(),
                entity.count(),
                entity.total()
        );
    }

    public ExerciseLibraryItem toDomain(ExerciseLibraryItemEntity entity) {
        return new ExerciseLibraryItem(
                entity.id(),
                entity.uuid(),
                entity.name(),
                entity.description(),
                entity.descriptionSource(),
                toDomain(entity.category()),
                entity.muscles().stream().map(this::toDomain).toList(),
                entity.secondaryMuscles().stream().map(this::toDomain).toList(),
                entity.equipment().stream().map(this::toDomain).toList(),
                entity.aliases(),
                entity.images().stream().map(this::toDomain).toList(),
                entity.videos().stream().map(this::toDomain).toList(),
                toDomain(entity.license()),
                entity.licenseAuthor()
        );
    }

    public List<ExerciseMuscle> toMuscleDomains(List<ExerciseMuscleEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }

    public List<ExerciseReference> toReferenceDomains(List<ExerciseReferenceEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }

    private ExerciseReference toDomain(ExerciseReferenceEntity entity) {
        return entity != null ? new ExerciseReference(entity.id(), entity.name()) : null;
    }

    private ExerciseMuscle toDomain(ExerciseMuscleEntity entity) {
        return new ExerciseMuscle(
                entity.id(),
                entity.name(),
                entity.nameEn(),
                entity.isFront(),
                entity.imageUrlMain(),
                entity.imageUrlSecondary()
        );
    }

    private ExerciseImage toDomain(ExerciseImageEntity entity) {
        return new ExerciseImage(entity.id(), entity.image(), entity.isMain());
    }

    private ExerciseVideo toDomain(ExerciseVideoEntity entity) {
        return new ExerciseVideo(entity.id(), entity.video(), entity.isMain(), entity.duration());
    }

    private ExerciseLicense toDomain(ExerciseLicenseEntity entity) {
        return entity != null
                ? new ExerciseLicense(entity.id(), entity.fullName(), entity.shortName(), entity.url())
                : null;
    }
}
