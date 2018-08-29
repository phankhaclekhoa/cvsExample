package csv;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.Iterator;

import com.opencsv.CSVReader;
import com.opencsv.bean.AbstractCSVToBean;
import com.opencsv.bean.CsvToBeanFilter;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;

public class IterableCSVToBean <T> extends AbstractCSVToBean implements Iterable<T> {
    private final MappingStrategy<T> strategy;
    private final CSVReader csvReader;
    private final CsvToBeanFilter filter;
    private boolean hasHeader;

    /**
     * IterableCSVToBean constructor
     *
     * @param csvReader CSVReader.  Should not be null.
     * @param strategy  MappingStrategy used to map CSV data to the bean.  Should not be null.
     * @param filter    Optional CsvToBeanFilter used remove unwanted data from reads.
     */
    public IterableCSVToBean(CSVReader csvReader, MappingStrategy<T> strategy, CsvToBeanFilter filter) {
        this.csvReader = csvReader;
        this.strategy = strategy;
        this.filter = filter;
        this.hasHeader = false;
    }

    /**
     * Retrieves the MappingStrategy.
     * @return The MappingStrategy being used by the IterableCSVToBean.
     */
    protected MappingStrategy<T> getStrategy() {
        return strategy;
    }

    /**
     * Retrieves the CSVReader.
     * @return The CSVReader being used by the IterableCSVToBean.
     */
    protected CSVReader getCSVReader() {
        return csvReader;
    }

    /**
     * Retrieves the CsvToBeanFilter
     *
     * @return The CsvToBeanFilter being used by the IterableCSVToBean.
     */
    protected CsvToBeanFilter getFilter() {
        return filter;
    }

    /**
     * Reads and processes a single line.
     * @return Object of type T with the requested information or null if there
     *   are no more lines to process.
     * @throws IllegalAccessException Thrown if there is a failure in introspection.
     * @throws InstantiationException Thrown when getting the PropertyDescriptor for the class.
     * @throws IOException Thrown when there is an unexpected error reading the file.
     * @throws IntrospectionException Thrown if there is a failure in introspection.
     * @throws InvocationTargetException Thrown if there is a failure in introspection.
     * @throws CsvRequiredFieldEmptyException 
     */
    public T nextLine() throws IllegalAccessException, InstantiationException,
            IOException, IntrospectionException, InvocationTargetException, CsvRequiredFieldEmptyException {
        if (!hasHeader) {
            strategy.captureHeader(csvReader);
            hasHeader = true;
        }
        T bean = null;
        String[] line;
        do {
            line = csvReader.readNext();
        } while (line != null && (filter != null && !filter.allowLine(line)));
        if (line != null) {
            bean = strategy.createBean();
            for (int col = 0; col < line.length; col++) {
                PropertyDescriptor prop = strategy.findDescriptor(col);
                if (null != prop) {
                    String value = checkForTrim(line[col], prop);
                    Object obj = convertValue(value, prop);
                    prop.getWriteMethod().invoke(bean, obj);
                }
            }
        }
        return bean;
    }


    private Iterator<T> iterator(final IterableCSVToBean<T> bean) {
        return new Iterator<T>() {
            private T nextBean;

            @Override
            public boolean hasNext() {
                if (nextBean != null) {
                    return true;
                }

                try {
                    nextBean = bean.nextLine();
                } catch (IllegalAccessException e) { // Replace with a multi-catch as soon as we support Java 7
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (CsvRequiredFieldEmptyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

                return nextBean != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                T holder = nextBean;
                nextBean = null;
                return holder;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("This is a read only iterator.");
            }
        };
    }

	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected PropertyEditor getPropertyEditor(PropertyDescriptor desc)
			throws InstantiationException, IllegalAccessException {
		// TODO Auto-generated method stub
		return null;
	}
}
