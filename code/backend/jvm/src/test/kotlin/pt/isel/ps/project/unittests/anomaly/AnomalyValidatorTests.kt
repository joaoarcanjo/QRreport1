package pt.isel.ps.project.unittests.anomaly

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import pt.isel.ps.project.exception.Errors
import pt.isel.ps.project.exception.InvalidParameter
import pt.isel.ps.project.exception.InvalidParameterException
import pt.isel.ps.project.model.anomaly.AnomalyEntity
import pt.isel.ps.project.model.anomaly.InputAnomalyEntity
import pt.isel.ps.project.util.Validator

class AnomalyValidatorTests {

    @Test
    fun `Create anomaly with valid anomaly`() {

        val anomaly = InputAnomalyEntity("Anomaly test")

        Assertions.assertThat(Validator.Device.Anomaly.verifyAnomalyInput(anomaly)).isTrue
    }

    @Test
    fun `Throws exception when anomaly is created with an invalid anomaly length`() {
        val invAnomaly = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"

        val anomaly = InputAnomalyEntity(invAnomaly)
        val expectedEx =
            InvalidParameterException(
                Errors.BadRequest.Message.INVALID_REQ_PARAMS,
                listOf(
                    InvalidParameter(
                        AnomalyEntity.ANOMALY_ANOMALY,
                        Errors.BadRequest.Locations.BODY,
                        Errors.BadRequest.Message.Device.Anomaly.INVALID_ANOMALY_ANOMALY_LENGTH
                    )
                )
            )

        Assertions.assertThatThrownBy { Validator.Device.Anomaly.verifyAnomalyInput(anomaly) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }

    @Test
    fun `Throws exception when anomaly is created with an blank anomaly`() {
        val invAnomaly = InputAnomalyEntity("    ")

        val expectedEx = InvalidParameterException(
            Errors.BadRequest.Message.BLANK_PARAMS_DETAIL,
            listOf(
                InvalidParameter(
                    AnomalyEntity.ANOMALY_ANOMALY,
                    Errors.BadRequest.Locations.BODY,
                    Errors.BadRequest.Message.BLANK_PARAMS
                )
            )
        )

        Assertions.assertThatThrownBy { Validator.Device.Anomaly.verifyAnomalyInput(invAnomaly) }
            .isInstanceOf(InvalidParameterException::class.java)
            .isEqualTo(expectedEx)
    }
}