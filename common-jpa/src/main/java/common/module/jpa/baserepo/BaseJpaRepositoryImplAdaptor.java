package common.module.jpa.baserepo;

import common.module.dto.BaseDTO;
import common.module.jpa.BaseJpaEntity;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class BaseJpaRepositoryImplAdaptor<E extends BaseJpaEntity, D extends BaseDTO> extends SimpleJpaRepository<E, String> {
    public BaseJpaRepositoryImplAdaptor(JpaEntityInformation<E, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }
}
