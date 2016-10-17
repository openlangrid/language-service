package jp.go.nict.langrid.wrapper.common.db.dao.entity;

import java.util.Calendar;

public class BasicEntity
extends DateManagedEntity{
	public BasicEntity(String[] values){
		this.values = values;
	}

	public BasicEntity(
			long id, String[] values
			, Calendar createdDateTime, Calendar updatedDateTime){
		super(id, createdDateTime, updatedDateTime);
		this.values = values;
	}

	protected String[] getValues(){
		return values;
	}

	protected void setValues(String[] values){
		this.values = values;
	}

	private String[] values;
}
