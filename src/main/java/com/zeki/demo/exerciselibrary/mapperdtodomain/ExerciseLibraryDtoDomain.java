package com.zeki.demo.exerciselibrary.mapperdtodomain;

import com.zeki.demo.exerciselibrary.domain.ExerciseImage;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibrary;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibraryItem;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibraryListCriteria;
import com.zeki.demo.exerciselibrary.domain.ExerciseLibrarySearchCriteria;
import com.zeki.demo.exerciselibrary.domain.ExerciseLicense;
import com.zeki.demo.exerciselibrary.domain.ExerciseMuscle;
import com.zeki.demo.exerciselibrary.domain.ExerciseReference;
import com.zeki.demo.exerciselibrary.domain.ExerciseVideo;
import com.zeki.demo.exerciselibrary.dto.ExerciseImageResponse;
import com.zeki.demo.exerciselibrary.dto.ExerciseLibraryItemResponse;
import com.zeki.demo.exerciselibrary.dto.ExerciseLibraryListRequest;
import com.zeki.demo.exerciselibrary.dto.ExerciseLibraryResponse;
import com.zeki.demo.exerciselibrary.dto.ExerciseLibrarySearchRequest;
import com.zeki.demo.exerciselibrary.dto.ExerciseLicenseResponse;
import com.zeki.demo.exerciselibrary.dto.ExerciseMuscleResponse;
import com.zeki.demo.exerciselibrary.dto.ExerciseReferenceResponse;
import com.zeki.demo.exerciselibrary.dto.ExerciseVideoResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ExerciseLibraryDtoDomain {

    public ExerciseLibrarySearchCriteria toDomain(ExerciseLibrarySearchRequest request) {
        return new ExerciseLibrarySearchCriteria(
                request.q(),
                request.limit(),
                request.offset(),
                request.muscleId(),
                request.equipmentId(),
                request.categoryId()
        );
    }

    public ExerciseLibraryListCriteria toDomain(ExerciseLibraryListRequest request) {
        return new ExerciseLibraryListCriteria(request.limit(), request.offset());
    }

    public ExerciseLibraryResponse toResponse(ExerciseLibrary library) {
        return new ExerciseLibraryResponse(
                library.results().stream().map(this::toResponse).toList(),
                library.count(),
                library.total()
        );
    }

    public ExerciseLibraryItemResponse toResponse(ExerciseLibraryItem item) {
        return new ExerciseLibraryItemResponse(
                item.id(),
                item.uuid(),
                item.name(),
                item.description(),
                item.descriptionSource(),
                toResponse(item.category()),
                item.muscles().stream().map(this::toResponse).toList(),
                item.secondaryMuscles().stream().map(this::toResponse).toList(),
                item.equipment().stream().map(this::toResponse).toList(),
                item.aliases(),
                item.images().stream().map(this::toResponse).toList(),
                item.videos().stream().map(this::toResponse).toList(),
                toResponse(item.license()),
                item.licenseAuthor()
        );
    }

    public List<ExerciseMuscleResponse> toMuscleResponses(List<ExerciseMuscle> muscles) {
        return muscles.stream().map(this::toResponse).toList();
    }

    public List<ExerciseReferenceResponse> toReferenceResponses(List<ExerciseReference> references) {
        return references.stream().map(this::toResponse).toList();
    }

    private ExerciseReferenceResponse toResponse(ExerciseReference reference) {
        return reference != null ? new ExerciseReferenceResponse(reference.id(), reference.name()) : null;
    }

    private ExerciseMuscleResponse toResponse(ExerciseMuscle muscle) {
        return new ExerciseMuscleResponse(
                muscle.id(),
                muscle.name(),
                muscle.nameEn(),
                muscle.isFront(),
                muscle.imageUrlMain(),
                muscle.imageUrlSecondary()
        );
    }

    private ExerciseImageResponse toResponse(ExerciseImage image) {
        return new ExerciseImageResponse(image.id(), image.image(), image.isMain());
    }

    private ExerciseVideoResponse toResponse(ExerciseVideo video) {
        return new ExerciseVideoResponse(video.id(), video.video(), video.isMain(), video.duration());
    }

    private ExerciseLicenseResponse toResponse(ExerciseLicense license) {
        return license != null
                ? new ExerciseLicenseResponse(license.id(), license.fullName(), license.shortName(), license.url())
                : null;
    }
}
