package redcoder.rctexteditor.utils;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void isIncludeNonEnglishLetter() {
        Assert.assertTrue(StringUtils.isContainNonEnglishLetter("fds-"));
        Assert.assertTrue(StringUtils.isContainNonEnglishLetter("换行"));
        Assert.assertTrue(StringUtils.isContainNonEnglishLetter("fsd换行"));
        Assert.assertTrue(StringUtils.isContainNonEnglishLetter("."));
    }
}
