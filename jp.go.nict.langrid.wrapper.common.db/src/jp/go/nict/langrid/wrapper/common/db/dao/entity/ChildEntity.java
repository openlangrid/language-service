package jp.go.nict.langrid.wrapper.common.db.dao.entity;

import java.util.Calendar;

public class ChildEntity
extends BasicEntity{
	public ChildEntity(long parentId, String[] values){
		super(values);
		this.parentId = parentId;
	}

	public ChildEntity(
			long id, long parentId, String[] values
			, Calendar createdDateTime, Calendar updatedDateTime){
		super(id, values, createdDateTime, updatedDateTime);
		this.parentId = parentId;
	}

	public long getParentId(){
		return parentId;
	}

	public void setParentId(long parentId){
		this.parentId = parentId;
	}

	private long parentId;
}
