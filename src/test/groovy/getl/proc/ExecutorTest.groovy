package getl.proc

import getl.utils.DateUtils
import getl.utils.Logs

class ExecutorTest extends getl.test.GetlTest {
    def list = [1, 2, 3, 4, 5]

    void testSingleThreadList() {
        def e = new Executor(countProc: 1, list: list)
        e.run { Logs.Fine("${DateUtils.FormatDate('HH:mm:ss.SSS', new Date())}: single $it ... ") }
    }

    void testManyThreadList() {
        def e = new Executor(countProc: 3, list: list)
        e.run { Logs.Fine("${DateUtils.FormatDate('HH:mm:ss.SSS', new Date())}: many $it ... ") }
    }

    void testMainCode() {
        def e = new Executor(countProc: 3, list: list, waitTime: 100)
        e.mainCode = {
            Logs.Fine("${DateUtils.FormatDate('HH:mm:ss.SSS', new Date())}: list $e.threadList")
        }
        e.run { println("${DateUtils.FormatDate('HH:mm:ss.SSS', new Date())}: child $it ... "); sleep(100) }
    }
}
