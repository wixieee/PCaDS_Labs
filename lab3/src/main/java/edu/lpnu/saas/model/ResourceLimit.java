package edu.lpnu.saas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "resource_limits")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceLimit extends BaseEntity {
    private Long organizationId;
    private Integer maxComments;

    @Builder.Default
    private Integer usedCommentsCount = 0;
}