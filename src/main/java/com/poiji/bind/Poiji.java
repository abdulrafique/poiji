package com.poiji.bind;

import com.poiji.bind.mapping.Unmarshaller;
import com.poiji.exception.IllegalCastException;
import com.poiji.exception.InvalidExcelFileExtension;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import com.poiji.option.PoijiOptions.PoijiOptionsBuilder;
import com.poiji.util.Files;

import java.io.File;
import java.util.List;

import static com.poiji.util.PoijiConstants.XLSX_EXTENSION;
import static com.poiji.util.PoijiConstants.XLS_EXTENSION;

/**
 * The entry point of the mapping process.
 * <p>
 * Example:
 * <pre>
 * List<Employee> employees = Poiji.fromExcel(new File("employees.xls"), Employee.class);
 * employees.size();
 * // 3
 * Employee firstEmployee = employees.get(0);
 * // Employee{employeeId=123923, name='Joe', surname='Doe', age=30, single=true, birthday='4/9/1987'}
 * </pre>
 * <p>
 * Created by hakan on 16/01/2017.
 */
public final class Poiji {

    private static final Files files = Files.getInstance();

    private Poiji() {
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param file
     *         excel file ending with .xls or .xlsx.
     * @param type
     *         type of the root object.
     * @param <T>
     *         type of the root object.
     * @return
     *         the newly created a list of objects
     *
     * @throws PoijiException
     *          if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension
     *          if the specified excel file extension is invalid.
     * @throws IllegalCastException
     *          if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     *
     * @see Poiji#fromExcel(File, Class, PoijiOptions)
     */
    public static synchronized <T> List<T>  fromExcel(final File file, final Class<T> type) {
        final Unmarshaller unmarshaller = deserializer(file, PoijiOptionsBuilder.settings().build());
        return unmarshaller.unmarshal(type);
    }

    /**
     * converts excel rows into a list of objects
     *
     * @param file
     *          excel file ending with .xls or .xlsx.
     * @param type
     *          type of the root object.
     * @param <T>
     *          type of the root object.
     * @param options
     *          specifies to change the default behaviour of the poiji.
     * @return
     *         the newly created a list of objects
     *
     * @throws PoijiException
     *         if an internal exception occurs during the mapping process.
     * @throws InvalidExcelFileExtension
     *          if the specified excel file extension is invalid.
     * @throws IllegalCastException
     *          if this Field object is enforcing Java language access control and the underlying field is either inaccessible or final.
     *
     * @see Poiji#fromExcel(File, Class)
     */
    public static synchronized <T> List<T> fromExcel(final File file, final Class<T> type, final PoijiOptions options) {
        final Unmarshaller unmarshaller = deserializer(file, options);
        return unmarshaller.unmarshal(type);
    }

    @SuppressWarnings("unchecked")
    private static Unmarshaller deserializer(final File file, final PoijiOptions options) {
        final PoijiFile poijiFile = new PoijiFile(file);

        String extension = files.getExtension(file.getName());

        if (XLS_EXTENSION.equals(extension)) {
            PoijiHSSHWorkbook poiWorkbookHSSH = new PoijiHSSHWorkbook(poijiFile);
            return Unmarshaller.instance(poiWorkbookHSSH, options);
        } else if (XLSX_EXTENSION.equals(extension)) {
            return Unmarshaller.instance(poijiFile, options);
        } else {
            throw new InvalidExcelFileExtension("Invalid file extension (" + extension + "), excepted .xls or .xlsx");
        }
    }
}
