package edu.lpnu.saas.audit.util.mapper;

import edu.lpnu.saas.audit.dto.ActivityLogResponse;
import edu.lpnu.saas.audit.dto.PageActivityLogResponse;
import edu.lpnu.saas.audit.dto.PageableObject;
import edu.lpnu.saas.audit.dto.SortObject;
import edu.lpnu.saas.audit.model.ActivityLog;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface ActivityLogMapper {
    ActivityLogResponse toResponse(ActivityLog log);

    default PageActivityLogResponse toPageResponse(Page<ActivityLog> page) {
        if (page == null) {
            return null;
        }

        PageActivityLogResponse response = new PageActivityLogResponse();

        response.setContent(page.getContent().stream().map(this::toResponse).toList());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setNumber(page.getNumber());
        response.setSize(page.getSize());
        response.setNumberOfElements(page.getNumberOfElements());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setEmpty(page.isEmpty());
        response.setPageable(toPageableObject(page.getPageable()));
        response.setSort(toSortObject(page.getSort()));

        return response;
    }

    default PageableObject toPageableObject(Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return null;
        }
        PageableObject obj = new PageableObject();
        obj.setOffset(pageable.getOffset());
        obj.setPageNumber(pageable.getPageNumber());
        obj.setPageSize(pageable.getPageSize());
        obj.setPaged(pageable.isPaged());
        obj.setUnpaged(pageable.isUnpaged());
        obj.setSort(toSortObject(pageable.getSort()));
        return obj;
    }

    default SortObject toSortObject(Sort sort) {
        if (sort == null) {
            return null;
        }
        SortObject obj = new SortObject();
        obj.setEmpty(sort.isEmpty());
        obj.setSorted(sort.isSorted());
        obj.setUnsorted(sort.isUnsorted());
        return obj;
    }

    default OffsetDateTime map(Instant value) {
        if (value == null) {
            return null;
        }
        return value.atOffset(ZoneOffset.UTC);
    }
}