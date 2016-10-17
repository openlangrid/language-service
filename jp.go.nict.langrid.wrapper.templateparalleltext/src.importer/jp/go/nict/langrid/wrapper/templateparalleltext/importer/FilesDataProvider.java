package jp.go.nict.langrid.wrapper.templateparalleltext.importer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import au.com.bytecode.opencsv.CSVReader;

public class FilesDataProvider implements Iterable<String[]>{
	public static void main(String[] args) throws Exception{
		for(String[] values : new FilesDataProvider("data/YMC Category.csv")){
			System.out.println(values[0]);
		}
	}

	public FilesDataProvider(String... files){
		this.files = Arrays.asList(files).iterator();
	}

	@Override
	public Iterator<String[]> iterator() {
		return new FileDataIterator();
	}

	class FileDataIterator implements Iterator<String[]>{
		@Override
		public boolean hasNext() {
			if(next != null) return true;
			try{
				fetchNext();
				return next != null;
			} catch(IOException e){
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

		private void fetchNext() throws IOException{
			if(finished) return;
			next = reader.readNext();
			while(next == null){
				reader.close();
				if(files.hasNext()){
					reader = new CSVReader(new InputStreamReader(
							new FileInputStream(files.next())
							, "UTF-8"));
					try{
						reader.readNext();
						next = reader.readNext();
					} catch(IOException e ){
						reader.close();
						throw e;
					}
				} else{
					finished = true;
					return;
				}
			}
		}

		private CSVReader reader = new CSVReader(new StringReader("")){
			public String[] readNext() throws IOException {
				return null;
			};
		};
		private String[] next;
		private boolean finished;
	}

	private Iterator<String> files;
}
