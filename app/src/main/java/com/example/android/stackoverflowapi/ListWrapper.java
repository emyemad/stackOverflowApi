package com.example.android.stackoverflowapi;

import java.util.List;

// The Stackoverflow API wraps replies for questions or answers in a JSON object with the name items.
// To handle this, create the following data class named ListWrapper. This is needed to handled the Stackoverflow items wrapper.
// This class accepts a type parameter and simply wraps a list of objects of that type.
public class ListWrapper<T> {

    List<T> items;
}
