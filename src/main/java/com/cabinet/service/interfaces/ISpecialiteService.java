package com.cabinet.service.interfaces;

import com.cabinet.entities.Specialite;

import java.util.List;

public interface ISpecialiteService {
    List<Specialite> findAll();
    Specialite findById(Long id);
    Specialite save(Specialite specialite);
    Specialite update(Long id, Specialite specialite);
    void delete(Long id);
}
