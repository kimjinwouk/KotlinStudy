package a.jinkim.trade.chatdetail

data class ChatItem(
    val senderId : String,
    val message: String
)
{
    constructor() : this("","")
}
