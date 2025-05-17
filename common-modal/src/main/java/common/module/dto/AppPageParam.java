package common.module.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppPageParam {
    private Integer pageSize = 10;
    private Integer pageIndex = 1;
    private String orderBy;
    private Boolean asc;

    public AppPageParam(Integer pageSize, Integer pageIndex) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
    }
}
