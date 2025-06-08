package com.codecool.demo.util;

import com.codecool.demo.model.LocalFile;

public interface LocalFileReader {
    LocalFile readFileTree(String path);
}
