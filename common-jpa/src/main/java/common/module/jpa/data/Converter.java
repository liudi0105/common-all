package common.module.jpa.data;

import common.module.jpa.GeneralJpaRepo;

public interface Converter<E, D> {

    Class<? extends GeneralJpaRepo<E, D, ?>> repoClz();

    D toD(E e);

    E toE(D e);
}
