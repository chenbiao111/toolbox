package com.shixin.app.parse.parser;

import com.shixin.app.parse.Parser;
import com.shixin.app.parse.callback.ParseCallback;

/**
 * ================================================
 * 作    者：Herve、Li
 * 创建日期：2021/3/15
 * 描    述：抖音解析器
 * 修订历史：
 * ================================================
 */
public class DouYinParser implements Parser {

    @Override
    public boolean parseHtml(String html, ParseCallback callback) {
        // 替换成无水印地址
        return videoBaseParse(html, callback, url -> url.replace("playwm", "play"));
    }
}
