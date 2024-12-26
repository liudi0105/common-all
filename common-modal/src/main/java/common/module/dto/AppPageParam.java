package common.module.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class AppPageParam {
    private Integer pageSize;
    private Integer pageIndex;
    private String orderBy;
    private Boolean asc;

    public AppPageParam(Integer pageSize, Integer pageIndex) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
    }
}
