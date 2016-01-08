package com.dhs.dao;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.dhs.dto.Data;

import java.util.*;

public class DataDao {
	HibernateTemplate template;

	public void setTemplate(HibernateTemplate template) {
		this.template = template;
	}

	// method to save Data
	public void saveData(Data e) {
		template.save(e);
	}

	// method to update Data
	public void updateData(Data e) {
		template.update(e);
	}

	// method to delete Data
	public void deleteData(Data e) {
		template.delete(e);
	}

	// method to return one Data of given id
	public Data getById(int id) {
		Data e = (Data) template.get(Data.class, id);
		return e;
	}

	// method to return all Data
	public List<Data> getData() {
		List<Data> list = new ArrayList<Data>();
		list = template.loadAll(Data.class);
		return list;
	}
	
	// method to saveOrUpdate Network Data
	public void  saveOrUpdateData(Data e) {
			template.saveOrUpdate(e);
		}
}