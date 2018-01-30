package boot67.common;

public class FDTFields {
	
	// Packet Relative Fields
	public final static int PacketType = 1;	
	public final static int PacketArray = 2001;
	public final static int Heartbeat = 5;
	public final static int SerialNumber = 2;
	public final static int WindCodeTable = 2013;
	public final static int WindMarketData = 2015;
	public final static int WindIndexData = 2016;
	public final static int WindFutureData = 2017;
	public final static int WindTransaction = 2019;
	public final static int WindMarkets = 2021;
	public final static int WindQuotationDateChange = 2022;
	public final static int WindMarketClose = 20023;
	public final static int WindHeartBeat = 20024;
	public final static int WindConnected = 20025;
	public final static int WindCodeTableResult = 2026;
	public final static int SnapShotEnds = 2027;
	public final static int HKStockData = 2028;


	public final static int ArrayOfString = 101;
	public final static int ArrayOfPacket = 102;
	
	
	// Quote Relative Fields

	public final static int SecurityExchange = 7;
	/**
	 * HK,US: 66:停牌  2：集合竞价
	 * A:
	 */
	public final static int Status = 11;
	public final static int Turnover = 13;
	public final static int Volume = 14;	
	public final static int BidPriceArray = 18;
	public final static int BidVolumeArray = 19;
	public final static int AskPriceArray = 20;
	public final static int AskVolumeArray = 21;
	public final static int Time = 23;
	public final static int Open = 25;
	public final static int ShortName = 26;
	public final static int EnglishName = 27;
	public final static int CNName =28;	
	public final static int High = 32;
	public final static int Last = 31;
	public final static int Low = 33;
	public final static int LastTradingDay = 36;
	public final static int Information = 37;
	public final static int IndexNumber = 38;
	public final static int BuySellFlag = 39;	
	public final static int PreClose = 40;	
	public final static int NumberOfTrades = 42;
	public final static int TotalBidVolume = 43;
	public final static int TotalAskVolume = 44;	
	public final static int LowLimit = 48;
	public final static int HighLimit = 49;	
	public final static int ActionDay = 51;
	public final static int WindSymbolCode = 55;	
	public final static int OpenInterest = 60;
	public final static int PreOpenInterest = 61;
	public final static int PreSettlePrice = 62;	
	public final static int SettlePrice = 63;	
	public final static int Close = 65;
	public final static int SecurityType = 67;  	
	public final static int TradingDay = 75;	
	public final static int BuyVolume = 80;
	public final static int BuyTurnover = 81;
	public final static int SellVolume = 82;
	public final static int SellTurnover = 83;
	public final static int UnclassifiedVolume = 84;
	public final static int UnclassifiedTurnover = 85;
	public final static int FTurnover = 86;
	public final static int WgtAvgAskPrice = 91;
	public final static int WgtAvgBidPrice = 92;
	public final static int YieldToMaturity = 93;
	public final static int Prefix = 94;
	public final static int Syl1 = 95;
	public final static int Syl2 = 96;
	public final static int SD2 = 97;
	public final static int DataCount = 98;
	public final static int HashCode = 99;
	
    public final static int SymbolName = 100;
    public final static int Group = 101;
    public final static int Product = 102;
    public final static int ProductName = 103;
    public final static int Currency = 104;	
    public final static int ShowID = 105;
    public final static int AskPrice = 120;
    public final static int AskVolume = 121;
    public final static int BidPrice = 118;
    public final static int BidVolume = 119;
    
    
    //======================================
	public final static int Multipler = 106;
	public final static int AskOrder = 201;
	public final static int BidOrder = 202;
	public final static int OrderKind = 203;
	public final static int ExchangeCode = 204;    /* code 实际上是 WindCode, 204 用作交易所的 code */
	public final static int Order = 205;   /* 委托号 */
	public final static int OrderPrice = 206;  /* 委托价格 */
	public final static int OrderVolume = 207; /* 委托数量 */
	public final static int FunctionCode = 208;/* 委托代码 B/S/C */
	public final static int Broker = 209;  /* 经纪商编码 */
	public final static int OrderStatus = 210; /* 委托状态 */
	public final static int OrderFlag = 211; /* 委托标识（PT市场）*/

	/* HK Warrants */
	public static final int UnderlyingCode = 302; /* 正股 */
	public static final int ExercisePrice = 303; /* 行权价 */
	public static final int ExerciseDate = 304; /* 行权日期 */
	public static final int IssueDate = 305; /* 发行日期 */
	public static final int ExpireDate = 306; /* 最后交易日 */
	public static final int ConversionRatio = 307; /* 换股比率 */
	public static final int OfficialPrice = 301; //按盘价
}
