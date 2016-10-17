package jp.go.nict.langrid.wrapper.templateparalleltext.importer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.Cell;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

public class GSheetDataProvider implements Iterable<String[]>{
	public GSheetDataProvider(String accessToken,
			String sheetName, String[] tabNames){
		this.accessToken = accessToken;
		this.sheetName = sheetName;
		this.tabNames = new HashSet<String>(Arrays.asList(tabNames));
	}

	static class NullIterator<T> implements Iterator<T>{
		@Override
		public boolean hasNext() {
			return false;
		}
		@Override
		public T next() {
			throw new NoSuchElementException();
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	class SheetRowIterator implements Iterator<String[]>{
		@Override
		public boolean hasNext() {
			if(next != null) return true;
			try{
				fetchNext();
				return next != null;
			} catch(IOException e){
				e.printStackTrace();
				return false;
			} catch(ServiceException e){
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public String[] next() {
			if(next == null){
				try{
					fetchNext();
				} catch(IOException e){
					throw new NoSuchElementException(e.getMessage());
				} catch(ServiceException e){
					throw new NoSuchElementException(e.getMessage());
				}
			}
			if(next == null){
				throw new NoSuchElementException();
			}
			String[] ret = next;
			next = null;
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private void fetchNext() throws IOException, ServiceException{
			if(finished) return;
			if(service == null){
				service = new SpreadsheetService(
						"templateparalleltext.importer"
						);
				service.setOAuth2Credentials(
						new GoogleCredential().setAccessToken(accessToken)
						);
			}
			if(worksheets == null){
				SpreadsheetQuery spreadsheetQuery = new SpreadsheetQuery(
						FeedURLFactory.getDefault().getSpreadsheetsFeedUrl());
				spreadsheetQuery.setTitleQuery(sheetName);
				SpreadsheetFeed sheets = service.query(spreadsheetQuery, SpreadsheetFeed.class);
				for(SpreadsheetEntry s : sheets.getEntries()){
					worksheets = s.getWorksheets().iterator();
					break;
				}
			}
			if(worksheets == null){
				finished = true;
				return;
			}
			if(!cells.hasNext()){
				if(!worksheets.hasNext()){
					finished = true;
					return;
				}
				do{
					WorksheetEntry s = worksheets.next();
					if(tabNames.contains(s.getTitle().getPlainText())){
						CellQuery cellQuery = new CellQuery(s.getCellFeedUrl());
						CellFeed cellFeed = service.query(cellQuery, CellFeed.class);
						List<String[]> cs = new ArrayList<String[]>();
						List<String> buff = new ArrayList<String>();
						int row = 2;
						for(CellEntry ce : cellFeed.getEntries()){
							Cell c = ce.getCell();
							String value = c.getValue();
							if(c.getRow() == 1) continue;
							if(value.startsWith("'")) value = value.substring(1);
							if(c.getRow() == row){
								for(int i = buff.size(); i < (c.getCol() - 1); i++) buff.add("");
								buff.add(value);
								continue;
							}
							cs.add(buff.toArray(new String[]{}));
							buff.clear();
							for(int i = buff.size(); i < (c.getCol() - 1); i++) buff.add("");
							buff.add(value);
							row++;
						}
						cells = cs.iterator();
						break;
					}
				} while(worksheets.hasNext());
			}
			if(!cells.hasNext()){
				finished = true;
				return;
			}
			next = cells.next();
		}

		private String[] next;
		private boolean finished;
		private SpreadsheetService service;
		private Iterator<WorksheetEntry> worksheets;
		private Iterator<String[]> cells = new NullIterator<String[]>();
	}

	@Override
	public Iterator<String[]> iterator(){
		return new SheetRowIterator();
	}

	private String accessToken;
	private String sheetName;
	private Set<String> tabNames;
}
