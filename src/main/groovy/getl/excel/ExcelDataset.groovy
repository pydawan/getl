/*
 GETL - based package in Groovy, which automates the work of loading and transforming data. His name is an acronym for "Groovy ETL".

 GETL is a set of libraries of pre-built classes and objects that can be used to solve problems unpacking,
 transform and load data into programs written in Groovy, or Java, as well as from any software that supports
 the work with Java classes.
 
 Copyright (C) EasyData Company LTD

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License and
 GNU Lesser General Public License along with this program.
 If not, see <http://www.gnu.org/licenses/>.
*/

package getl.excel

import getl.data.*
import getl.exception.ExceptionGETL
import getl.utils.*
import groovy.transform.InheritConstructors

/**
 * Excel Dataset class
 * @author Dmitry Shaldin
 */
@InheritConstructors
class ExcelDataset extends Dataset {
    @Override
    void setConnection(Connection value) {
        if (value != null && !(value instanceof ExcelConnection))
            throw new ExceptionGETL('Сonnection to ExcelConnection class is allowed!')

        super.setConnection(value)
    }

    /** Use specified connection */
    ExcelConnection useConnection(ExcelConnection value) {
        setConnection(value)
        return value
    }

    /** Current Excel connection */
    ExcelConnection getCurrentExcelConnection() { connection as ExcelConnection }

    /** List name */
    String getListName () { params.listName as String }
    /** List name */
    void setListName (final String value) { params.listName = value }

    /** List number */
    Integer getListNumber() { params.listNumber as Integer }
    /** List number */
    void setListNumber(final Integer value) { params.listNumber = value }

    /** Offset param */
    Map<String, Integer> getOffset() { params.offset as Map<String, Integer> }
    /** Offset param */
    void setOffset(final Map<String, Integer> value) { params.offset = value }

    /** Limit rows to return */
    Integer getLimit() { params.limit as Integer }
    /** Limit rows to return */
    void setLimit(final Integer value) { params.limit = value }

    /** Header row */
    Boolean getHeader() {
        BoolUtils.IsValue([params.header, currentExcelConnection?.header], false)
    }
    /** Header row */
    void setHeader(final boolean value) { params.header = value }

    /** Warnings from Dataset (e.g. show warning when list not found) */
    Boolean getShowWarnings() {
        BoolUtils.IsValue([params.showWarnings, currentExcelConnection?.showWarnings], false)
    }
    /** Warnings from Dataset (e.g. show warning when list not found) */
    void setShowWarnings(final Boolean value) { params.showWarnings = value}

    @Override
	String getObjectName() { objectFullName }
    
	@Override
	String getObjectFullName() { "${fullFileName()}~[$listName]" }

    /** Full file name with path */
    String fullFileName() {
        currentExcelConnection.currentExcelDriver.fullFileNameDataset(this)
    }
}