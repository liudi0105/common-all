package common.module.jpa;

import common.module.jpa.baserepo.BaseJpaRepositoryImpl;
import common.module.jpa.condition.QueryManager;
import common.module.jpa.data.Converter;
import jakarta.persistence.EntityManager;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.util.List;

public class BaseJpaRepositoryFactoryBean<R extends BaseJpaRepo<T, ?>, T extends BaseJpaEntity> extends JpaRepositoryFactoryBean<R, T, String> {

    @Autowired
    private QueryManager queryManager;

    @Autowired
    private BaseJpaRepoAdapter baseJpaRepoAdapter;

    private Class<?> repositoryInterface;

    @Autowired(required = false)
    private List<Converter<?, ?>> converters;

    public BaseJpaRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
        this.repositoryInterface = repositoryInterface;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        if (converters == null) {
            this.converters = Lists.newArrayList();
        }
        Converter<?, ?> converter = converters.stream().filter(v -> v.repoClz().equals(repositoryInterface))
                .findAny()
                .orElse(null);
        return new JpaRepositoryFactory(entityManager) {
            protected JpaRepositoryImplementation<T, String> getTargetRepository(RepositoryInformation information, EntityManager entityManager1) {
                JpaEntityInformation entityInformation = getEntityInformation(information.getDomainType());
                BaseJpaRepositoryImpl baseJpaRepository = new BaseJpaRepositoryImpl<>(
                        entityInformation,
                        queryManager,
                        baseJpaRepoAdapter,
                        AppEntityManager.newInstance(entityManager1),
                        repositoryInterface,
                        converter,
                        new SimpleJpaRepository<>(entityInformation, entityManager1)
                );
                return baseJpaRepository;
            }

            @Override
            protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
                return BaseJpaRepositoryImpl.class;
            }
        };
    }
}
