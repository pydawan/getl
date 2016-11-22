package getl.utils

/**
 * @author Alexsey Konstantinov
 */
class DateUtilsTest extends GroovyTestCase {
    void testParseDate() {
        assertNull(DateUtils.ParseDate('yyyy-MM-dd HH:mm:ss', null))

        assertNotNull(DateUtils.ParseDate('yyyy-MM-dd HH:mm:ss.SSS', textDateTime))
        assertNull(DateUtils.ParseDate('yyyy-MM-dd HH:mm:ss.SSS', '2016-13-32 24:61:80.999', true))
    }

    void testClearTime() {
        assertNull(DateUtils.ClearTime(null))

        assertEquals(exampleDate, DateUtils.ClearTime(exampleDateTime))
    }

    void testTruncTime() {
        assertNull(DateUtils.TruncTime(Calendar.HOUR, null))

        assertEquals(exampleDateHour, DateUtils.TruncTime(Calendar.HOUR, exampleDateTime))
        assertEquals(exampleDateHourMinute, DateUtils.TruncTime(Calendar.MINUTE, exampleDateTime))
    }

    void testFormatDate() {
        assertNull(DateUtils.FormatDate('yyyy-MM-dd HH:mm:ss.SSS', null))

        assertEquals(textDateTime, DateUtils.FormatDate('yyyy-MM-dd HH:mm:ss.SSS', exampleDateTime))
    }

    void testAddDate() {
        assertNull(DateUtils.AddDate('yyyy', 1, null))

        def d = DateUtils.ParseDateTime('2016-01-01 00:00:00.000')
        assertEquals(DateUtils.ParseDateTime('2017-01-01 00:00:00.000'), DateUtils.AddDate('yyyy', 1, d))
        assertEquals(DateUtils.ParseDateTime('2016-02-01 00:00:00.000'), DateUtils.AddDate('MM', 1, d))
        assertEquals(DateUtils.ParseDateTime('2016-01-02 00:00:00.000'), DateUtils.AddDate('dd', 1, d))
        assertEquals(DateUtils.ParseDateTime('2016-01-01 01:00:00.000'), DateUtils.AddDate('HH', 1, d))
        assertEquals(DateUtils.ParseDateTime('2016-01-01 00:01:00.000'), DateUtils.AddDate('mm', 1, d))
        assertEquals(DateUtils.ParseDateTime('2016-01-01 00:00:01.000'), DateUtils.AddDate('ss', 1, d))
    }

    void testPartOfDate() {
        assertEquals(0, DateUtils.PartOfDate('YEAR', null))
        assertEquals(0, DateUtils.PartOfDate(null, exampleDate))

        def d = exampleDateTime
        assertEquals(2016, DateUtils.PartOfDate('YEAR', d))
        assertEquals(12, DateUtils.PartOfDate('MONTH', d))
        assertEquals(31, DateUtils.PartOfDate('DAY_OF_MONTH', d))
        assertEquals(23, DateUtils.PartOfDate('HOUR', d))
        assertEquals(58, DateUtils.PartOfDate('MINUTE', d))
        assertEquals(59, DateUtils.PartOfDate('SECOND', d))
    }

    void testTimestamp2Value() {
        assertNull(DateUtils.Timestamp2Value(null))
        def b = BigDecimal.valueOf(1483217939.000000000)
        assertEquals(b, DateUtils.Timestamp2Value(exampleDateTime))
    }

    void testValue2Timestamp() {
        assertNull(DateUtils.Value2Timestamp(null))
        def b = BigDecimal.valueOf(1483217939.000000000)
        assertEquals(exampleDateTime, DateUtils.Value2Timestamp(b))
    }

    void testPeriodCrossing() {
        assertEquals([start: null, finish: null], DateUtils.PeriodCrossing(null))
        def p = [
                    [start: DateUtils.ParseDate('2016-01-01'), finish: DateUtils.ParseDate('2016-01-04')],
                    [start: DateUtils.ParseDate('2016-01-03'), finish: DateUtils.ParseDate('2016-01-06')],
                    [start: DateUtils.ParseDate('2016-01-02'), finish: DateUtils.ParseDate('2016-01-05')]
        ]
        assertEquals([start: DateUtils.ParseDate('2016-01-01'), finish: DateUtils.ParseDate('2016-01-06')], DateUtils.PeriodCrossing(p))
    }

    final def textDate = '2016-12-31'
    final def textDateTime = '2016-12-31 23:58:59.000'

    Date getExampleDate() { DateUtils.ParseDate(textDate) }
    Date getExampleDateTime() { DateUtils.ParseDateTime(textDateTime) }
    Date getExampleDateHour() { DateUtils.ParseDateTime(textDateTime.substring(0, 13) + ':00:00.000') }
    Date getExampleDateHourMinute() { DateUtils.ParseDateTime(textDateTime.substring(0, 16) + ':00.000') }
}
