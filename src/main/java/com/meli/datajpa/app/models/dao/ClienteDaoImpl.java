package com.meli.datajpa.app.models.dao;

import com.meli.datajpa.app.models.entity.Cliente;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository("clienteDaoJPA")
public class ClienteDaoImpl implements IClienteDao {

    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings("unchecked")
    @Transactional(readOnly=true)
    @Override
    public List<Cliente> findAll() {
        return em.createQuery("from Cliente").getResultList();
    }

    @Override
    @Transactional
    public void save(Cliente cliente) {
        em.persist(cliente);
    }
}
