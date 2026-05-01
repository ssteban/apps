package com.example.neuroinicial.data

import com.example.neuroinicial.models.TestResult

/**
 * A simple singleton to hold the selected result for display in the Report screen.
 * This is a lightweight alternative to a full ViewModel/Repository setup for now.
 */
object SharedData {
    var selectedTestResult: TestResult? = null
}
