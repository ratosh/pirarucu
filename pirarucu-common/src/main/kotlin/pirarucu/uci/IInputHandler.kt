package pirarucu.uci

interface IInputHandler {

    fun setOption(option: String, value: String)

    fun position(tokens: Array<String>)

    fun search(tokens: Array<String>)

    fun stop()

    fun isReady()
}