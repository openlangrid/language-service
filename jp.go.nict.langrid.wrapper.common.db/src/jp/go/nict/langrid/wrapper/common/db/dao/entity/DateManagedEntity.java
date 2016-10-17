package jp.go.nict.langrid.wrapper.common.db.dao.entity;

import java.util.Calendar;

import jp.go.nict.langrid.commons.util.CalendarUtil;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class DateManagedEntity {
	public DateManagedEntity(){
	}

	public DateManagedEntity(long id
			, Calendar createdDateTime, Calendar updatedDateTime){
		this.id = id;
		this.createdDateTime = createdDateTime;
		this.updatedDateTime = updatedDateTime;
	}

	@Override
	public boolean equals(Object value){
		return EqualsBuilder.reflectionEquals(this, value);
	}

	@Override
	public int hashCode(){
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString(){
		ReflectionToStringBuilder b = new ReflectionToStringBuilder(this);
		b.setExcludeFieldNames(new String[]{"createdDateTime", "updatedDateTime"});
		b.append("createdDateTime", createdDateTime != null ?
				CalendarUtil.formatToDefault(createdDateTime) :
				null);
		b.append("updatedDateTime", updatedDateTime != null ?
				CalendarUtil.formatToDefault(updatedDateTime) :
				null);
		return b.toString();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Calendar getCreatedDateTime() {
		return createdDateTime;
	}

	public void setCreatedDateTime(Calendar createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Calendar getUpdatedDateTime() {
		return updatedDateTime;
	}

	public void setUpdatedDateTime(Calendar updatedDateTime) {
		this.updatedDateTime = updatedDateTime;
	}

	private long id;
	private Calendar createdDateTime;
	private Calendar updatedDateTime;
}
