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

package getl.lang.opts

import getl.exception.ExceptionGETL
import getl.lang.Getl
import getl.utils.MapUtils

import java.util.concurrent.ConcurrentHashMap

/**
 * Base options class
 * @author Alexsey Konstantinov
 *
 */
class BaseSpec {
    BaseSpec() {
        initSpec()
    }

    BaseSpec(def ownerObject, def thisObject) {
        this.ownerObject = ownerObject
        this.thisObject = thisObject
        initSpec()
    }

    BaseSpec(def ownerObject, def thisObject, Boolean useExternalParams, Map<String, Object> importParams) {
        this.ownerObject = ownerObject
        this.thisObject = thisObject
        if (importParams != null) {
            if (useExternalParams) {
                _params = importParams
            } else {
                importFromMap(importParams)
            }
        }
        initSpec()
    }

    /** Init options after create object */
    protected void initSpec() { }

    /** Detect delegate object for closure code */
    static Object DetectClosureDelegate(Object obj) {
        while (obj instanceof Closure) obj = (obj as Closure).delegate
        return obj
    }

    /** This object for this object */
    protected def thisObject

    /** This object for owner object */
    protected def ownerObject

    /** Preparing closure code for this object */
    Closure prepareClosure(Closure cl) {
        Closure res
        if (thisObject instanceof Getl)
            res = Getl.PrepareClosure(ownerObject?:this, thisObject?:this, this, cl)
        else {
            cl.setDelegate(this)
            cl.setResolveStrategy(Closure.DELEGATE_FIRST)
            res = cl
        }

        return res
    }

    /** Preparing closure code for specified object */
    Closure prepareClosure(def parent, Closure cl) {
        Closure res
        if (thisObject instanceof Getl)
            res = Getl.PrepareClosure(ownerObject?:this, thisObject?:this, parent?:this, cl)
        else {
            cl.setDelegate(parent)
            cl.setResolveStrategy(Closure.DELEGATE_FIRST)
            res = cl
        }

        return res
    }

    /** Run closure for this object */
    void runClosure(Closure cl) {
        if (cl == null) return
        if (thisObject instanceof Getl)
            Getl.RunClosure(ownerObject?:this, thisObject?:this, this, cl)
        else {
            cl.setDelegate(this)
            cl.setResolveStrategy(Closure.DELEGATE_FIRST)
            cl.call(this)
        }
    }

    /** Run closure for specified object */
    void runClosure(def parent, Closure cl) {
        if (cl == null) return
        if (thisObject instanceof Getl)
            Getl.RunClosure(ownerObject?:this, thisObject?:this, parent?:this, cl)
        else {
            cl.setDelegate(parent)
            cl.setResolveStrategy(Closure.DELEGATE_FIRST)
            cl.call(parent)
        }
    }

    Map<String, Object> _params = new ConcurrentHashMap<String, Object>()
    /** Object parameters */
    Map<String, Object> getParams() { _params }

    /**
     * Detected ignore key map from import
     */
    @SuppressWarnings("GrMethodMayBeStatic")
    protected List<String> ignoreImportKeys(Map<String, Object> importParams) { [] as List<String> }

    /**
     * Import options from map
     */
    void importFromMap(Map<String, Object> importParams) {
        if (importParams == null) throw new ExceptionGETL('Required "importParams" value!')
        params.putAll(MapUtils.Copy(importParams, ignoreImportKeys(importParams)))
    }
}