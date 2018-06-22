package com.yuanhan.yuanhan.core.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import com.yuanhan.yuanhan.core.pojo.BaseObject;

/**
 * 
 * @author:		yuanhan
 * @since:		2017-10-05
 * @modified:	2017-10-05
 * @version:	
 */
public class CommonRepositoryFactoryBean<T extends CommonRepository<BO,ID>, BO extends BaseObject, ID extends Serializable>
		extends JpaRepositoryFactoryBean<T,BO,ID>
{
	
	private BeanFactory beanFactory;
	
	/**
	 * Creates a new {@link CommonRepositoryFactoryBean} for the given repository interface.
	 * 
	 * @param repositoryInterface must not be {@literal null}.
	 */
    public CommonRepositoryFactoryBean(Class<? extends T> repositoryInterface)
	{
		super(repositoryInterface);
		
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		super.setBeanFactory(beanFactory);
	}

	@Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new CommonRepositoryFactory<T,BO,ID>(entityManager, beanFactory);
    }

    private static class CommonRepositoryFactory<T extends CommonRepository<BO,ID>, BO extends BaseObject, ID extends Serializable> extends JpaRepositoryFactory {
    	

    	private BeanFactory beanFactory;

        public CommonRepositoryFactory(EntityManager entityManager, BeanFactory beanFactory) {
            super(entityManager);
            this.beanFactory = beanFactory;
        }

		@Override
		@SuppressWarnings("unchecked")
    	protected Object getTargetRepository(RepositoryInformation information) {
			T t = (T)super.getTargetRepository(information);
			return t;
    	}

		@Override
		protected <R, KEY extends Serializable> SimpleJpaRepository<?, ?> getTargetRepository(
				RepositoryInformation information, EntityManager entityManager) 
		{
			JpaEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
			return getTargetRepositoryViaReflection(information, entityInformation, entityManager, beanFactory);
		}

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return CommonRepositoryImpl.class;
        }
    }
}
