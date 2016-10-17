package jp.go.nict.langrid.wrapper.common.db.dao.entity;

import java.util.Calendar;

public class CategorizedEntity
extends BasicEntity{
	public CategorizedEntity(String[] values, Category[] categories){
		super(values);
		this.categories = categories;
	}

	public CategorizedEntity(
			long id, String[] values
			, Category[] categories
			, Calendar createdDateTime, Calendar updatedDateTime){
		super(id, values, createdDateTime, updatedDateTime);
		this.categories = categories;
	}

	public Category[] getCategories() {
		return categories;
	}

	public void setCategories(Category[] categories) {
		this.categories = categories;
	}

	private Category[] categories;
}
