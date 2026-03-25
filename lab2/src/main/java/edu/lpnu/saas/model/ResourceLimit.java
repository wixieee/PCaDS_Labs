package edu.lpnu.saas.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResourceLimit extends BaseEntity{
    private Long organizationId;
    private Integer maxComments;
    @Builder.Default
    private Integer usedCommentsCount = 0;
}
