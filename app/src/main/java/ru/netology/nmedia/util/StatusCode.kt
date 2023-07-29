package ru.netology.nmedia.util

enum class StatusCode (val code: Int) {
    Continue(100),
    SwitchingProtocols(101),
    Processing(102),

    OK(200),
    Created(201),
    Accepted(202),
    NonAuthoritativeInformation(203),
    NoContent(204),
    ResetContent(205),
    PartialContent(206),
    MultiStatus(207),
    AlreadyReported(208),
    IMUsed(226),

    MultipleChoices(300),
    MovedPermanently(301),
    Found(302),
    SeeOther(303),
    NotModified(304),
    UseProxy(305),
    TemporaryRedirect(307),
    PermanentRedirect(308),

    BadRequest(400),
    Unauthorized(401),
    PaymentRequired(402),
    Forbidden(403),
    NotFound(404),
    MethodNotAllowed(405),
    NotAcceptable(406),
    ProxyAuthenticationRequired(407),
    RequestTimeout(408),
    Conflict(409),
    Gone(410),
    LengthRequired(411),
    PreconditionFailed(412),
    PayloadTooLarge(413),
    UriTooLong(414),
    UnsupportedMediaType(415),
    RangeNotSatisfiable(416),
    ExpectationFailed(417),
    IAmATeapot(418),
    MisdirectedRequest(421),
    UnprocessableEntity(422),
    Locked(423),
    FailedDependency(424),
    UpgradeRequired(426),
    PreconditionRequired(428),
    TooManyRequests(429),
    RequestHeaderFieldsTooLarge(431),
    UnavailableForLegalReasons(451),

    InternalServerError(500),
    NotImplemented(501),
    BadGateway(502),
    ServiceUnavailable(503),
    GatewayTimeout(504),
    HttpVersionNotSupported(505),
    VariantAlsoNegotiates(506),
    InsufficientStorage(507),
    LoopDetected(508),
    NotExtended(510),
    NetworkAuthenticationRequired(511),

    Unknown(0)
}

object StatusCodeCompanion{
    fun httpCode(code: Int): StatusCode {
        return when (code) {
            300 -> StatusCode.MultipleChoices
            301 -> StatusCode.MovedPermanently
            302 -> StatusCode.Found
            303 -> StatusCode.SeeOther
            304 -> StatusCode.NotModified
            305 -> StatusCode.UseProxy
            307 -> StatusCode.TemporaryRedirect
            308 -> StatusCode.PermanentRedirect

            400 -> StatusCode.BadRequest
            401 -> StatusCode.Unauthorized
            402 -> StatusCode.PaymentRequired
            403 -> StatusCode.Forbidden
            404 -> StatusCode.NotFound
            405 -> StatusCode.MethodNotAllowed
            406 -> StatusCode.NotAcceptable
            407 -> StatusCode.ProxyAuthenticationRequired
            408 -> StatusCode.RequestTimeout
            409 -> StatusCode.Conflict
            410 -> StatusCode.Gone
            411 -> StatusCode.LengthRequired
            412 -> StatusCode.PreconditionFailed
            413 -> StatusCode.PayloadTooLarge
            414 -> StatusCode.UriTooLong
            415 -> StatusCode.UnsupportedMediaType
            416 -> StatusCode.RangeNotSatisfiable
            417 -> StatusCode.ExpectationFailed
            418 -> StatusCode.IAmATeapot
            421 -> StatusCode.MisdirectedRequest
            422 -> StatusCode.UnprocessableEntity
            423 -> StatusCode.Locked
            424 -> StatusCode.FailedDependency
            426 -> StatusCode.UpgradeRequired
            428 -> StatusCode.PreconditionRequired
            429 -> StatusCode.TooManyRequests
            431 -> StatusCode.RequestHeaderFieldsTooLarge
            451 -> StatusCode.UnavailableForLegalReasons

            500 -> StatusCode.InternalServerError
            501 -> StatusCode.NotImplemented
            502 -> StatusCode.BadGateway
            503 -> StatusCode.ServiceUnavailable
            504 -> StatusCode.GatewayTimeout
            505 -> StatusCode.HttpVersionNotSupported
            506 -> StatusCode.VariantAlsoNegotiates
            507 -> StatusCode.InsufficientStorage
            508 -> StatusCode.LoopDetected
            510 -> StatusCode.NotExtended
            511 -> StatusCode.NetworkAuthenticationRequired
            else -> StatusCode.Unknown
        }
    }
}