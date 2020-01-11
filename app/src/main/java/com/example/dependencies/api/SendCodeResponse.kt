package com.example.dependencies.api

data class SendCodeResponse(val message: String,
                            val success: Boolean,
                            val value: Int)