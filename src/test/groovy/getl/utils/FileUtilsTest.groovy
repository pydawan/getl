package getl.utils

import getl.tfs.TFS
import groovy.transform.Synchronized
import net.lingala.zip4j.model.enums.AesKeyStrength
import net.lingala.zip4j.model.enums.CompressionLevel
import net.lingala.zip4j.model.enums.EncryptionMethod
import org.junit.Test

/**
 * @author Alexsey Konstantinov
 */
class FileUtilsTest extends getl.test.GetlTest {
    @Test
    void testFileExtension() {
        assertEquals('txt', FileUtils.FileExtension('test.txt'))
        assertEquals('txt', FileUtils.FileExtension('/tmp/test.getl/test.txt'))
    }

    @Test
    void testExcludeFileExtension() {
        assertEquals(FileUtils.ConvertToDefaultOSPath('test'), FileUtils.ExcludeFileExtension('test.txt'))
        assertEquals(FileUtils.ConvertToDefaultOSPath('/tmp/test.getl/test'), FileUtils.ExcludeFileExtension('/tmp/test.getl/test.txt'))
    }

    @Test
    void testRenameTo() {
        def source = "${TFS.systemPath}/${FileUtils.UniqueFileName()}"
        def destFileName = 'test_rename.txt'
        def dest = "${TFS.systemPath}/$destFileName"

        new File(source).text = 'test'
        FileUtils.RenameTo(source, destFileName)

        assertTrue(FileUtils.DeleteFile(dest))
    }

    @Test
    void testMoveTo() {
        def fileName = 'test_move.txt'
        def source = "${TFS.systemPath}/$fileName"
        def destPath = "${TFS.systemPath}/test_move"

        new File(source).text = 'test'
        shouldFail { FileUtils.MoveTo(source, destPath, false) }
        FileUtils.MoveTo(source, destPath)
        assertEquals('test', new File("$destPath/$fileName").text)

        new File(source).text = 'test 1'
        FileUtils.MoveTo(source, destPath)
        assertEquals('test 1', new File("$destPath/$fileName").text)

        assertTrue(FileUtils.DeleteFile("$destPath/$fileName"))
        assertTrue(FileUtils.DeleteDir(destPath))
    }

    @Test
    void testCopyToDir() {
        def fileName = 'test_copy.txt'
        def source = "${TFS.systemPath}/$fileName"
        def destPath = "${TFS.systemPath}/test_copy"

        def f = new File(source)
        f.deleteOnExit()
        f.text = 'test'
        shouldFail { FileUtils.CopyToDir(source, destPath, false) }
        FileUtils.CopyToDir(source, destPath)

        assertTrue(FileUtils.DeleteFile("$destPath/$fileName"))
        assertTrue(FileUtils.DeleteDir(destPath))
    }

    @Test
    void testCopyToFile() {
        def fileName = 'test_copy.txt'
        def source = "${TFS.systemPath}/$fileName"
        def destPath = "${TFS.systemPath}/test_copy"
        def destFileName = "$destPath/test_copy_new.txt"

        def f = new File(source)
        f.deleteOnExit()
        f.text = 'test'
        shouldFail { FileUtils.CopyToFile(source, destFileName, false) }
        FileUtils.CopyToFile(source, destFileName)

        assertTrue(FileUtils.DeleteFile(destFileName))
        assertTrue(FileUtils.DeleteDir(destPath))
    }

    @Test
    void testDeleteFolder() {
        def path = "${TFS.systemPath}/test_empty_folder"
        FileUtils.ValidPath(path)
        FileUtils.ValidPath("$path/1/a")
        FileUtils.ValidPath("$path/1/b")
        FileUtils.ValidPath("$path/1/c")
        FileUtils.ValidPath("$path/2")

        new File("$path/1/a/test_empty.txt").text = 'test'
        new File("$path/1/b/test_empty.txt").text = 'test'

        FileUtils.DeleteEmptyFolder(path, true)
        assertTrue(new File("$path/1").exists())
        assertFalse(new File("$path/2").exists())

        assertTrue(FileUtils.DeleteFolder("$path/1", true))
        assertFalse(new File("$path/1").exists())

        FileUtils.DeleteEmptyFolder(path, true)
        assertFalse(new File(path).exists())
    }

    @Test
    void testLastDirFromPath() {
        assertNull(FileUtils.lastDirFromPath(null as String))
        assertEquals('test.getl', FileUtils.lastDirFromPath("/tmp/test.getl"))
    }

    @Test
    void testLastDirFromFile() {
        assertNull(FileUtils.lastDirFromFile(null as String))

        def path = "${TFS.systemPath}/test.getl"
        def fileName = "$path/test.txt"

        FileUtils.ValidFilePath(fileName)
        new File(fileName).text = 'test'

        assertEquals('test.getl', FileUtils.lastDirFromFile(fileName))

        FileUtils.DeleteFolder(path, true)
    }

    @Test
    void testMaskFile() {
        assertNull(FileUtils.MaskFile(null))
        assertNull(FileUtils.MaskFile('/tmp/test.getl/test123_a.txt'))
        assertEquals('*.txt', FileUtils.MaskFile('*.txt'))
        assertEquals('test???_*.txt', FileUtils.MaskFile('/tmp/test.getl/test???_*.txt'))
    }

    @Test
    void testPathFromFile() {
        assertNull(FileUtils.PathFromFile(null))

        def p = FileUtils.ConvertToDefaultOSPath(TFS.systemPath)
        def f = new File("$p/test_pathfromfile.txt")
        f.deleteOnExit()
        f.text = 'test'

        assertEquals(p, FileUtils.PathFromFile("$p/test_pathfromfile.txt"))
        assertEquals(p, FileUtils.PathFromFile("$p/test_*.txt"))
    }

    @Test
    void testFileName() {
        assertNull(FileUtils.FileName(null))

        def p = FileUtils.ConvertToDefaultOSPath(TFS.systemPath)
        def f = new File("$p/test_pathfromfile.txt")
        f.deleteOnExit()
        f.text = 'test'

        assertEquals('test_pathfromfile.txt', FileUtils.FileName("$p/test_pathfromfile.txt"))
        assertEquals('test_*.txt', FileUtils.FileName("$p/test_*.txt"))
    }

    @Test
    void testIsLockFileForRead() {
        assertNull(FileUtils.IsLockFileForRead(null))

        def fileName = "${TFS.systemPath}/test_lock.txt"
        def f = new File(fileName)
        f.deleteOnExit()
        f.text = 'test'

        def lf = FileUtils.LockFile(fileName, 'rw', false)
        assertTrue(FileUtils.IsLockFileForRead(fileName))
        lf.release()
        lf.channel().close()

        assertFalse(FileUtils.IsLockFileForRead(fileName))
    }

    @Test
    void testConvertText() {
        def original_text = new StringReader('Converted text 12345 test validation')
        def processed_text = new StringWriter()
        def rules = [
                [type: 'REGEXPR', old: 'text.*test', new: 'text-test'],
                [type: 'REPLACE', old: 'e', new: '[E]']
        ]
        FileUtils.ConvertText(original_text, processed_text, rules, null, null)
        assertEquals('Conv[E]rt[E]d t[E]xt-t[E]st validation\n', processed_text.toString())
    }

    @Test
    void testRun() {
        //return
        def sout = new StringBuilder()
        def serr = new StringBuilder()
        if (Config.isWindows()) {
            FileUtils.Run('cmd /c dir', TFS.systemPath, 'utf-8', sout, serr)
        }
        else {
            FileUtils.Run('ls', TFS.systemPath, 'utf-8', sout, serr)
        }
    }

    @Test
    void testCompressToZip() {
        def fileName = "${TFS.systemPath}/${FileUtils.UniqueFileName()}"

        new File(fileName + '.txt').text = 'test zip archive'
        FileUtils.CompressToZip(fileName + '.zip', fileName + '.txt',
                [compressionMethod: 'DEFLATE',
                 compressionLevel: 'MAXIMUM',
                 encryptFiles: true,
                 encryptionMethod: 'AES',
                 aesKeyStrength: 'KEY_STRENGTH_256',
                 password: 'TEST GETL ZIP'])

        assertTrue(FileUtils.ExistsFile(fileName + '.zip'))
    }

    @Test
    void testParseArguments() {
        assertEquals(['1', '2', '3'], FileUtils.ParseArguments('1 2 3'))
        assertEquals(['1', '2', '3'], FileUtils.ParseArguments('1  2  3'))
        assertEquals(['"1"', '2', '3', '4', '5'], FileUtils.ParseArguments('"1" 2 3 4 5'))
        assertEquals(['1', '2', '"3"', '4', '5'], FileUtils.ParseArguments('1 2 "3" 4 5'))
        assertEquals(['1', '2', '3', '4', '"5"'], FileUtils.ParseArguments('1 2 3 4 "5"'))
        assertEquals(['"1 2 3"', '4', '5'], FileUtils.ParseArguments('"1 2 3" 4 5'))
        assertEquals(['1', '"2 3 4"', '5'], FileUtils.ParseArguments('1 "2 3 4" 5'))
        assertEquals(['1', '2', '"3 4 5"'], FileUtils.ParseArguments('1 2 "3 4 5"'))
        assertEquals(['1', '2', '" 3  4  5 "'], FileUtils.ParseArguments('1 2 " 3  4  5 "'))
        shouldFail { FileUtils.ParseArguments('1 2 "3 4 5') }
    }

    @Test
    void testClassLoaser() {
        shouldFail { FileUtils.ClassLoaderFromPath('tests/test.jar') }

        if (!FileUtils.ExistsFile('tests/xero/demo.jar')) return
        def classLoader = FileUtils.ClassLoaderFromPath('tests/xero/*.jar')
        def url = classLoader.getResource('xero.conf')
        assertNotNull(url)
        assertNotNull(url.text)
    }

    @Test
    void testFindParentPath() {
        assertNotNull(FileUtils.FindParentPath('.','getl\\.'))
        assertTrue(FileUtils.ExistsFile(FileUtils.FindParentPath('.', 'getl\\.') + 'getl/src'))
    }

    @Test
    void testReadFileFromResource() {
        def file1 = FileUtils.FileFromResources('/fileutils/file.txt')
        assertEquals('1234567890', file1.text)

        def file2 = FileUtils.FileFromResources('fileutils/file.txt', null, this.getClass().classLoader)
        assertEquals('1234567890', file2.text)

        assertFalse(file1 == file2)

        def file3 = FileUtils.FileFromResources('/fileutils/file.txt')
        assertEquals('1234567890', file3.text)

        assertEquals(file1, file3)
    }

    @Test
    void testParseFileName() {
        def fileName1 = FileUtils.ResourceFileName('resource:/fileutils/file.txt')
        assertTrue(new File(fileName1).exists())

        def fileName2 = FileUtils.ResourceFileName(fileName1)
        assertEquals(fileName1, fileName2)
    }

    @Test
    void testFileMaskExpression() {
        def list = [
                'dir1-1/file*.ext': 'dir1\\-1/file.*[.]ext',
                'dir?-?\\file+1.*': 'dir.\\-.\\\\file\\+1[.].*',
                '%file*^.???': '[%]file.*[^][.]...'
        ]

        list.each { mask, rule ->
            println "$mask: $rule"
            assertEquals("$mask: $rule", rule, FileUtils.FileMaskToMathExpression(mask))
        }
    }
}