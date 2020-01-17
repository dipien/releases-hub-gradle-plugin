package com.releaseshub.gradle.plugin.core

import org.junit.Assert
import org.junit.Test

class FileSizeFormatterTest {

    @Test
    fun test() {
        Assert.assertEquals("5 bytes", FileSizeFormatter.format(5))
        Assert.assertEquals("1023 bytes", FileSizeFormatter.format(1023))
        Assert.assertEquals("1 KB", FileSizeFormatter.format(1024))
        Assert.assertEquals("1.01 KB", FileSizeFormatter.format(1035))
        Assert.assertEquals("1.5 KB", FileSizeFormatter.format(1536))
        Assert.assertEquals("1024 KB", FileSizeFormatter.format(1048575))
        Assert.assertEquals("1 MB", FileSizeFormatter.format(1048576))
        Assert.assertEquals("15.74 MB", FileSizeFormatter.format(16499876))
        Assert.assertEquals("10 MB", FileSizeFormatter.format(10485760))
    }
}
