package getl.lang

import getl.utils.BoolUtils
import getl.utils.SynchronizeObject
import groovy.transform.BaseScript
import groovy.transform.Field

@BaseScript Getl main

@Field def enabled; assert enabled != null
@Field def checkOnStart; assert checkOnStart != null
@Field def checkForThreads; assert checkForThreads != null

options {
    processControlDataset = embeddedTable {
        field('name') { length = 128; isKey = true }
        field('enabled') { type = booleanFieldType; isNull = false }
        tableName = 'testDslAllowProcess'
        drop(ifExists: true)
        create()

        rowsTo {
            writeRow { add ->
                add name: 'getl.lang.DslTestAllowProcess', enabled: BoolUtils.IsValue(enabled)
            }
        }
    }

    checkProcessOnStart = BoolUtils.IsValue(checkOnStart)
    checkProcessForThreads = BoolUtils.IsValue(checkForThreads)
}

configContent.testAllowProcess = true

def c = new SynchronizeObject()

thread {
    useList (1..9)
    run(3) {
        c.nextCount()
    }
}

configContent.testAllowThreads = c.count