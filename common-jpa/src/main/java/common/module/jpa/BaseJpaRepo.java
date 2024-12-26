package common.module.jpa;

import common.module.dto.BaseDTO;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseJpaRepo<E extends BaseJpaEntity, D extends BaseDTO> extends GeneralJpaRepo<E, D, String> {

}
