package com.tosspayments.paymentsdk.sample.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.gson.Gson
import com.tosspayments.paymentsdk.ComposePaymentWidget
import com.tosspayments.paymentsdk.model.PaymentCallback
import com.tosspayments.paymentsdk.model.TossPaymentResult
import com.tosspayments.paymentsdk.sample.databinding.TossPaymentLayoutBinding
import com.tosspayments.paymentsdk.view.PaymentMethod

class PaymentWidgetComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaymentScreen()
            // 결제 승인 api
            // https://docs.tosspayments.com/reference/test/v1/payments/confirm/POST
        }
    }
}

@Composable
fun PaymentScreen() {
    val activityResultRegistryOwner = LocalActivityResultRegistryOwner.current
    val activityResultRegistry = activityResultRegistryOwner?.activityResultRegistry

    val composePaymentWidget = remember {
        ComposePaymentWidget(
            activityResultRegistry = activityResultRegistry!!,
            clientKey = "test_ck_vZnjEJeQVxagOxR6pQ6OrPmOoBN0",
            customerKey = "custom"
        )
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    val paymentMethod = composePaymentWidget.getSelectedPaymentMethod()
                    Log.d("paymentMethod", paymentMethod.toString())

                    composePaymentWidget.requestPayment(
                        paymentInfo = PaymentMethod.PaymentInfo("SZT0FdY4Q1QQ9BxNymziau", "토스 티셔츠 외 2건"),
                        paymentCallback = object : PaymentCallback {
                            val gson = Gson()
                            override fun onPaymentSuccess(success: TossPaymentResult.Success) {
                                Log.d("onPaymentSuccess", gson.toJson(success))
                            }

                            override fun onPaymentFailed(fail: TossPaymentResult.Fail) {
                                Log.d("onPaymentFailed", gson.toJson(fail))
                            }
                        }
                    )
                },
                shape = RoundedCornerShape(15.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(55.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0069FF)),
            ) {
                androidx.compose.material.Text(
                    text = "결제하기",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValue ->
        AndroidViewBinding(
            factory = { inflater, parent, attachToParent ->
                val view = TossPaymentLayoutBinding.inflate(inflater, parent, attachToParent)
                composePaymentWidget.apply {
                    renderPaymentMethods(
                        method = view.paymentWidget,
                        amount = PaymentMethod.Rendering.Amount(
                            value = 15000,
                            currency = PaymentMethod.Rendering.Currency.KRW,
                            country = "KR"
                        )
                    )
                    renderAgreement(agreement = view.paymentAgreement)
                }
                view
            },
            update = {},
            modifier = Modifier.padding(paddingValue)
        )
    }
}