package common.module.jpa;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.core.EntityInformation;

import java.util.List;
import java.util.Map;

//@Component
public class JpaQueryExecutor {

    private Map<String, EntityInformation<?, String>> entityInformation;

    private EntityManager entityManager;

    @Autowired
    public void setEntityInformation(List<BaseJpaEntity> baseJpaEntity) {
        System.out.println();
    }
}
