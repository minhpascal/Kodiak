package com.clearpool.kodiak.feedlibrary.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.clearpool.kodiak.feedlibrary.core.bx.BxNormalizer;
import com.clearpool.kodiak.feedlibrary.core.cta.CqsNormalizer;
import com.clearpool.kodiak.feedlibrary.core.cta.CtsNormalizer;
import com.clearpool.kodiak.feedlibrary.core.nasdaq.NasdaqNormalizer;
import com.clearpool.kodiak.feedlibrary.core.opra.OpraNormalizer;
import com.clearpool.kodiak.feedlibrary.core.psx.PsxNormalizer;
import com.clearpool.kodiak.feedlibrary.core.utp.UqdfNormalizer;
import com.clearpool.kodiak.feedlibrary.core.utp.UtdfNormalizer;

public class MdFeedProps
{
	private static final Map<String, String> props = new HashMap<String, String>();
	private static final Map<String, Object> instanceProps = new HashMap<String, Object>();

	// Keys
	public static final String SEQUENCER = "SEQUENCER";
	public static final String NORMALIZER = "NORMALIZER";
	public static final String RANGE = "RANGE";

	static
	{
		props.put("TEST.1.A", "239.9.9.10:9010");
		props.put("TEST.2.A", "239.9.9.11:9011");

		props.put("OPRA." + NORMALIZER, OpraNormalizer.class.getCanonicalName());
		props.put("OPRA.1.RANGE", "[A|A-AAPL|L]");
		props.put("OPRA.1.A", "233.43.202.1:11101");
		props.put("OPRA.1.B", "233.43.202.33:12101");
		props.put("OPRA.2.RANGE", "[AAPL|M-ADLZZ|X]");
		props.put("OPRA.2.A", "233.43.202.2:11102");
		props.put("OPRA.2.B", "233.43.202.34:12102");
		props.put("OPRA.3.RANGE", "[ADM|A-ALZZZ|X]");
		props.put("OPRA.3.A", "233.43.202.3:11103");
		props.put("OPRA.3.B", "233.43.202.35:12103");
		props.put("OPRA.4.RANGE", "[AM|A-APZZZ|X]");
		props.put("OPRA.4.A", "233.43.202.4:11104");
		props.put("OPRA.4.B", "233.43.202.36:12104");
		props.put("OPRA.5.RANGE", "[AQ|A-BAZZZ|X]");
		props.put("OPRA.5.A", "233.43.202.5:11105");
		props.put("OPRA.5.B", "233.43.202.37:12105");
		props.put("OPRA.6.RANGE", "[BB|A-BOZZZ|X]");
		props.put("OPRA.6.A", "233.43.202.6:11106");
		props.put("OPRA.6.B", "233.43.202.38:12106");
		props.put("OPRA.7.RANGE", "[BP|A-CCZZZ|X]");
		props.put("OPRA.7.A", "233.43.202.7:11107");
		props.put("OPRA.7.B", "233.43.202.39:12107");
		props.put("OPRA.8.RANGE", "[CD|A-CMG|L]");
		props.put("OPRA.8.A", "233.43.202.8:11108");
		props.put("OPRA.8.B", "233.43.202.40:12108");
		props.put("OPRA.9.RANGE", "[CMG|M-CSZZZ|X]");
		props.put("OPRA.9.A", "233.43.202.9:11109");
		props.put("OPRA.9.B", "233.43.202.41:12109");
		props.put("OPRA.10.RANGE", "[CT|A-DEZZZ|X]");
		props.put("OPRA.10.A", "233.43.202.10:11110");
		props.put("OPRA.10.B", "233.43.202.42:12110");
		props.put("OPRA.11.RANGE", "[DF|A-DIAZZ|X]");
		props.put("OPRA.11.A", "233.43.202.11:11111");
		props.put("OPRA.11.B", "233.43.202.43:12111");
		props.put("OPRA.12.RANGE", "[DIB|A-EDMZZ|X]");
		props.put("OPRA.12.A", "233.43.202.12:11112");
		props.put("OPRA.12.B", "233.43.202.44:12112");
		props.put("OPRA.13.RANGE", "[EDN|A-ERZZZ|X]");
		props.put("OPRA.13.A", "233.43.202.13:11113");
		props.put("OPRA.13.B", "233.43.202.45:12113");
		props.put("OPRA.14.RANGE", "[ES|A-FAS|L]");
		props.put("OPRA.14.A", "233.43.202.14:11114");
		props.put("OPRA.14.B", "233.43.202.46:12114");
		props.put("OPRA.15.RANGE", "[FAS|M-FLZZZ|X]");
		props.put("OPRA.15.A", "233.43.202.15:11115");
		props.put("OPRA.15.B", "233.43.202.47:12115");
		props.put("OPRA.16.RANGE", "[FM|A-GDZZZ|X]");
		props.put("OPRA.16.A", "233.43.202.16:11116");
		props.put("OPRA.16.B", "233.43.202.48:12116");
		props.put("OPRA.17.RANGE", "[GE|A-GLD|L]");
		props.put("OPRA.17.A", "233.43.202.17:11117");
		props.put("OPRA.17.B", "233.43.202.49:12117");
		props.put("OPRA.18.RANGE", "[GLD|M-GOLGZ|X]");
		props.put("OPRA.18.A", "233.43.202.18:11118");
		props.put("OPRA.18.B", "233.43.202.50:12118");
		props.put("OPRA.19.RANGE", "[GOLH|A-GOOG|X]");
		props.put("OPRA.19.A", "233.43.202.19:11119");
		props.put("OPRA.19.B", "233.43.202.51:12119");
		props.put("OPRA.20.RANGE", "[GOOGA|A-HAZZZ|X]");
		props.put("OPRA.20.A", "233.43.202.20:11120");
		props.put("OPRA.20.B", "233.43.202.52:12120");
		props.put("OPRA.21.RANGE", "[HB|A-IKZZZ|X]");
		props.put("OPRA.21.A", "233.43.202.21:11121");
		props.put("OPRA.21.B", "233.43.202.53:12121");
		props.put("OPRA.22.RANGE", "[IL|A-IWM|L]");
		props.put("OPRA.22.A", "233.43.202.22:11122");
		props.put("OPRA.22.B", "233.43.202.54:12122");
		props.put("OPRA.23.RANGE", "[IWM|M-JNJZZ|X]");
		props.put("OPRA.23.A", "233.43.202.23:11123");
		props.put("OPRA.23.B", "233.43.202.55:12123");
		props.put("OPRA.24.RANGE", "[JNK|A-LEKZZ|X]");
		props.put("OPRA.24.A", "233.43.202.24:11124");
		props.put("OPRA.24.B", "233.43.202.56:12124");
		props.put("OPRA.25.RANGE", "[LEL|A-MA|L]");
		props.put("OPRA.25.A", "233.43.202.129:16101");
		props.put("OPRA.25.B", "233.43.202.161:17101");
		props.put("OPRA.26.RANGE", "[MA|M-MMZZZ|X]");
		props.put("OPRA.26.A", "233.43.202.130:16102");
		props.put("OPRA.26.B", "233.43.202.162:17102");
		props.put("OPRA.27.RANGE", "[MN|A-MUZZZ|X]");
		props.put("OPRA.27.A", "233.43.202.131:16103");
		props.put("OPRA.27.B", "233.43.202.163:17103");
		props.put("OPRA.28.RANGE", "[MV|A-NFLX|L]");
		props.put("OPRA.28.A", "233.43.202.132:16104");
		props.put("OPRA.28.B", "233.43.202.164:17104");
		props.put("OPRA.29.RANGE", "[NFLX|M-ODZZZ|X]");
		props.put("OPRA.29.A", "233.43.202.133:16105");
		props.put("OPRA.29.B", "233.43.202.165:17105");
		props.put("OPRA.30.RANGE", "[OE|A-PCLN|L]");
		props.put("OPRA.30.A", "233.43.202.134:16106");
		props.put("OPRA.30.B", "233.43.202.166:17106");
		props.put("OPRA.31.RANGE", "[PCLN|M-PMZZZ|X]");
		props.put("OPRA.31.A", "233.43.202.135:16107");
		props.put("OPRA.31.B", "233.43.202.167:17107");
		props.put("OPRA.32.RANGE", "[PN|A-QQQ|L]");
		props.put("OPRA.32.A", "233.43.202.136:16108");
		props.put("OPRA.32.B", "233.43.202.168:17108");
		props.put("OPRA.33.RANGE", "[QQQ|M-RTZZZ|X]");
		props.put("OPRA.33.A", "233.43.202.137:16109");
		props.put("OPRA.33.B", "233.43.202.169:17109");
		props.put("OPRA.34.RANGE", "[RU|A-SHZZZ|X]");
		props.put("OPRA.34.A", "233.43.202.138:16110");
		props.put("OPRA.34.B", "233.43.202.170:17110");
		props.put("OPRA.35.RANGE", "[SI|A-SPXZZ|X]");
		props.put("OPRA.35.A", "233.43.202.139:16111");
		props.put("OPRA.35.B", "233.43.202.171:17111");
		props.put("OPRA.36.RANGE", "[SPY|A-SPY|L]");
		props.put("OPRA.36.A", "233.43.202.140:16112");
		props.put("OPRA.36.B", "233.43.202.172:17112");
		props.put("OPRA.37.RANGE", "[SPY|M-SPY|X]");
		props.put("OPRA.37.A", "233.43.202.141:16113");
		props.put("OPRA.37.B", "233.43.202.173:17113");
		props.put("OPRA.38.RANGE", "[SPYA|A-SVXZZ|X]");
		props.put("OPRA.38.A", "233.43.202.142:16114");
		props.put("OPRA.38.B", "233.43.202.174:17114");
		props.put("OPRA.39.RANGE", "[SVY|A-TKZZZ|X]");
		props.put("OPRA.39.A", "233.43.202.143:16115");
		props.put("OPRA.39.B", "233.43.202.175:17115");
		props.put("OPRA.40.RANGE", "[TL|A-TSLA|L]");
		props.put("OPRA.40.A", "233.43.202.144:16116");
		props.put("OPRA.40.B", "233.43.202.176:17116");
		props.put("OPRA.41.RANGE", "[TSLA|M-UMZZZ|X]");
		props.put("OPRA.41.A", "233.43.202.145:16117");
		props.put("OPRA.41.B", "233.43.202.177:17117");
		props.put("OPRA.42.RANGE", "[UN|A-UZZZZ|X]");
		props.put("OPRA.42.A", "233.43.202.146:16118");
		props.put("OPRA.42.B", "233.43.202.178:17118");
		props.put("OPRA.43.RANGE", "[V|A-VXZZZ|X]");
		props.put("OPRA.43.A", "233.43.202.147:16119");
		props.put("OPRA.43.B", "233.43.202.179:17119");
		props.put("OPRA.44.RANGE", "[VY|A-WFZZZ|X]");
		props.put("OPRA.44.A", "233.43.202.148:16120");
		props.put("OPRA.44.B", "233.43.202.180:17120");
		props.put("OPRA.45.RANGE", "[WG|A-XLDZZ|X]");
		props.put("OPRA.45.A", "233.43.202.149:16121");
		props.put("OPRA.45.B", "233.43.202.181:17121");
		props.put("OPRA.46.RANGE", "[XLE|A-XLJZZ|X]");
		props.put("OPRA.46.A", "233.43.202.150:16122");
		props.put("OPRA.46.B", "233.43.202.182:17122");
		props.put("OPRA.47.RANGE", "[XLK|A-XMZZZ|X]");
		props.put("OPRA.47.A", "233.43.202.151:16123");
		props.put("OPRA.47.B", "233.43.202.183:17123");
		props.put("OPRA.48.RANGE", "[XN|A-ZZZZZ|X]");
		props.put("OPRA.48.A", "233.43.202.152:16124");
		props.put("OPRA.48.B", "233.43.202.184:17124");

		props.put("CQS." + NORMALIZER, CqsNormalizer.class.getCanonicalName());
		props.put("CQS.1.RANGE", "[A-ANZZZZ]");
		props.put("CQS.1.A", "233.200.79.0:61000");
		props.put("CQS.1.B", "233.200.79.32:61032");
		props.put("CQS.2.RANGE", "[AO-BXZZZZ]");
		props.put("CQS.2.A", "233.200.79.1:61001");
		props.put("CQS.2.B", "233.200.79.33:61033");
		props.put("CQS.3.RANGE", "[BY-CRZZZZ]");
		props.put("CQS.3.A", "233.200.79.2:61002");
		props.put("CQS.3.B", "233.200.79.34:61034");
		props.put("CQS.4.RANGE", "[CS-ELZZZZ]");
		props.put("CQS.4.A", "233.200.79.3:61003");
		props.put("CQS.4.B", "233.200.79.35:61035");
		props.put("CQS.5.RANGE", "[EM-GLZZZZ]");
		props.put("CQS.5.A", "233.200.79.4:61004");
		props.put("CQS.5.B", "233.200.79.36:61036");
		props.put("CQS.6.RANGE", "[GM-IQZZZZ]");
		props.put("CQS.6.A", "233.200.79.5:61005");
		props.put("CQS.6.B", "233.200.79.37:61037");
		props.put("CQS.7.RANGE", "[IR-LVZZZZ]");
		props.put("CQS.7.A", "233.200.79.6:61006");
		props.put("CQS.7.B", "233.200.79.38:61038");
		props.put("CQS.8.RANGE", "[LW-NOZZZZ]");
		props.put("CQS.8.A", "233.200.79.7:61007");
		props.put("CQS.8.B", "233.200.79.39:61039");
		props.put("CQS.9.RANGE", "[NP-PRZZZZ]");
		props.put("CQS.9.A", "233.200.79.8:61008");
		props.put("CQS.9.B", "233.200.79.40:61040");
		props.put("CQS.10.RANGE", "[PS-STZZZZ]");
		props.put("CQS.10.A", "233.200.79.9:61009");
		props.put("CQS.10.B", "233.200.79.41:61041");
		props.put("CQS.11.RANGE", "[SU-USZZZZ]");
		props.put("CQS.11.A", "233.200.79.10:61010");
		props.put("CQS.11.B", "233.200.79.42:61042");
		props.put("CQS.12.RANGE", "[UT-ZZZZZZ]");
		props.put("CQS.12.A", "233.200.79.11:61011");
		props.put("CQS.12.B", "233.200.79.43:61043");
		props.put("CQS.13.RANGE", "[A-DZZZZZ]");
		props.put("CQS.13.A", "233.200.79.16:61016");
		props.put("CQS.13.B", "233.200.79.48:61048");
		props.put("CQS.14.RANGE", "[E-EWZZZZ]");
		props.put("CQS.14.A", "233.200.79.17:61017");
		props.put("CQS.14.B", "233.200.79.49:61049");
		props.put("CQS.15.RANGE", "[EX-GZZZZZ]");
		props.put("CQS.15.A", "233.200.79.18:61018");
		props.put("CQS.15.B", "233.200.79.50:61050");
		props.put("CQS.16.RANGE", "[H-IWEZZZ]");
		props.put("CQS.16.A", "233.200.79.19:61019");
		props.put("CQS.16.B", "233.200.79.51:61051");
		props.put("CQS.17.RANGE", "[IWF-KIZZZZ]");
		props.put("CQS.17.A", "233.200.79.20:61020");
		props.put("CQS.17.B", "233.200.79.52:61052");
		props.put("CQS.18.RANGE", "[KJ-RMZZZZ]");
		props.put("CQS.18.A", "233.200.79.21:61021");
		props.put("CQS.18.B", "233.200.79.53:61053");
		props.put("CQS.19.RANGE", "[RN-SKZZZZ]");
		props.put("CQS.19.A", "233.200.79.22:61022");
		props.put("CQS.19.B", "233.200.79.54:61054");
		props.put("CQS.20.RANGE", "[SL-SPZZZZ]");
		props.put("CQS.20.A", "233.200.79.23:61023");
		props.put("CQS.20.B", "233.200.79.55:61055");
		props.put("CQS.21.RANGE", "[SQ-UMZZZZ]");
		props.put("CQS.21.A", "233.200.79.24:61024");
		props.put("CQS.21.B", "233.200.79.56:61056");
		props.put("CQS.22.RANGE", "[UN-VNZZZZ]");
		props.put("CQS.22.A", "233.200.79.25:61025");
		props.put("CQS.22.B", "233.200.79.57:61057");
		props.put("CQS.23.RANGE", "[VO-XLEZZZ]");
		props.put("CQS.23.A", "233.200.79.26:61026");
		props.put("CQS.23.B", "233.200.79.58:61058");
		props.put("CQS.24.RANGE", "[XLF-ZZZZZZ]");
		props.put("CQS.24.A", "233.200.79.27:61027");
		props.put("CQS.24.B", "233.200.79.59:61059");

		props.put("CTS." + NORMALIZER, CtsNormalizer.class.getCanonicalName());
		props.put("CTS.1.RANGE", "[A-ANZZZZ]");
		props.put("CTS.1.A", "233.200.79.128:62128");
		props.put("CTS.1.B", "233.200.79.160:62160");
		props.put("CTS.2.RANGE", "[AO-BXZZZZ]");
		props.put("CTS.2.A", "233.200.79.129:62129");
		props.put("CTS.2.B", "233.200.79.161:62161");
		props.put("CTS.3.RANGE", "[BY-CRZZZZ]");
		props.put("CTS.3.A", "233.200.79.130:62130");
		props.put("CTS.3.B", "233.200.79.162:62162");
		props.put("CTS.4.RANGE", "[CS-ELZZZZ]");
		props.put("CTS.4.A", "233.200.79.131:62131");
		props.put("CTS.4.B", "233.200.79.163:62163");
		props.put("CTS.5.RANGE", "[EM-GLZZZZ]");
		props.put("CTS.5.A", "233.200.79.132:62132");
		props.put("CTS.5.B", "233.200.79.164:62164");
		props.put("CTS.6.RANGE", "[GM-IQZZZZ]");
		props.put("CTS.6.A", "233.200.79.133:62133");
		props.put("CTS.6.B", "233.200.79.165:62165");
		props.put("CTS.7.RANGE", "[IR-LVZZZZ]");
		props.put("CTS.7.A", "233.200.79.134:62134");
		props.put("CTS.7.B", "233.200.79.166:62166");
		props.put("CTS.8.RANGE", "[LW-NOZZZZ]");
		props.put("CTS.8.A", "233.200.79.135:62135");
		props.put("CTS.8.B", "233.200.79.167:62167");
		props.put("CTS.9.RANGE", "[NP-PRZZZZ]");
		props.put("CTS.9.A", "233.200.79.136:62136");
		props.put("CTS.9.B", "233.200.79.168:62168");
		props.put("CTS.10.RANGE", "[PS-STZZZZ]");
		props.put("CTS.10.A", "233.200.79.137:62137");
		props.put("CTS.10.B", "233.200.79.169:62169");
		props.put("CTS.11.RANGE", "[SU-USZZZZ]");
		props.put("CTS.11.A", "233.200.79.138:62138");
		props.put("CTS.11.B", "233.200.79.170:62170");
		props.put("CTS.12.RANGE", "[UT-ZZZZZZ]");
		props.put("CTS.12.A", "233.200.79.139:62139");
		props.put("CTS.12.B", "233.200.79.171:62171");
		props.put("CTS.13.RANGE", "[A-DZZZZZ]");
		props.put("CTS.13.A", "233.200.79.144:62144");
		props.put("CTS.13.B", "233.200.79.176:62176");
		props.put("CTS.14.RANGE", "[E-EWZZZZ]");
		props.put("CTS.14.A", "233.200.79.145:62145");
		props.put("CTS.14.B", "233.200.79.177:62177");
		props.put("CTS.15.RANGE", "[EX-GZZZZZ]");
		props.put("CTS.15.A", "233.200.79.146:62146");
		props.put("CTS.15.B", "233.200.79.178:62178");
		props.put("CTS.16.RANGE", "[H-IWEZZZ]");
		props.put("CTS.16.A", "233.200.79.147:62147");
		props.put("CTS.16.B", "233.200.79.179:62179");
		props.put("CTS.17.RANGE", "[IWF-KIZZZZ]");
		props.put("CTS.17.A", "233.200.79.148:62148");
		props.put("CTS.17.B", "233.200.79.180:62180");
		props.put("CTS.18.RANGE", "[KJ-RMZZZZ]");
		props.put("CTS.18.A", "233.200.79.149:62149");
		props.put("CTS.18.B", "233.200.79.181:62181");
		props.put("CTS.19.RANGE", "[RN-SKZZZZ]");
		props.put("CTS.19.A", "233.200.79.150:62150");
		props.put("CTS.19.B", "233.200.79.182:62182");
		props.put("CTS.20.RANGE", "[SL-SPZZZZ]");
		props.put("CTS.20.A", "233.200.79.151:62151");
		props.put("CTS.20.B", "233.200.79.183:62183");
		props.put("CTS.21.RANGE", "[SQ-UMZZZZ]");
		props.put("CTS.21.A", "233.200.79.152:62152");
		props.put("CTS.21.B", "233.200.79.184:62184");
		props.put("CTS.22.RANGE", "[UN-VNZZZZ]");
		props.put("CTS.22.A", "233.200.79.153:62153");
		props.put("CTS.22.B", "233.200.79.185:62185");
		props.put("CTS.23.RANGE", "[VO-XLEZZZ]");
		props.put("CTS.23.A", "233.200.79.154:62154");
		props.put("CTS.23.B", "233.200.79.186:62186");
		props.put("CTS.24.RANGE", "[XLF-ZZZZZZ]");
		props.put("CTS.24.A", "233.200.79.155:62155");
		props.put("CTS.24.B", "233.200.79.187:62187");

		props.put("UQDF." + NORMALIZER, UqdfNormalizer.class.getName());
		props.put("UQDF.1.RANGE", "[A-CD]");
		props.put("UQDF.1.A", "224.0.17.48:55530");
		props.put("UQDF.1.B", "224.0.17.49:55531");
		props.put("UQDF.2.RANGE", "[CE-FD]");
		props.put("UQDF.2.A", "224.0.17.50:55532");
		props.put("UQDF.2.B", "224.0.17.51:55533");
		props.put("UQDF.3.RANGE", "[FE-LK]");
		props.put("UQDF.3.A", "224.0.17.52:55534");
		props.put("UQDF.3.B", "224.0.17.53:55535");
		props.put("UQDF.4.RANGE", "[LL-PB]");
		props.put("UQDF.4.A", "224.0.17.54:55536");
		props.put("UQDF.4.B", "224.0.17.55:55537");
		props.put("UQDF.5.RANGE", "[PC-SP]");
		props.put("UQDF.5.A", "224.0.17.56:55538");
		props.put("UQDF.5.B", "224.0.17.57:55539");
		props.put("UQDF.6.RANGE", "[SQ-ZZ]");
		props.put("UQDF.6.A", "224.0.17.58:55540");
		props.put("UQDF.6.B", "224.0.17.59:55541");

		props.put("UTDF." + NORMALIZER, UtdfNormalizer.class.getName());
		props.put("UTDF.1.RANGE", "[A-CD]");
		props.put("UTDF.1.A", "224.0.1.92:55542");
		props.put("UTDF.1.B", "224.0.1.93:55543");
		props.put("UTDF.2.RANGE", "[CE-FD]");
		props.put("UTDF.2.A", "224.0.1.94:55544");
		props.put("UTDF.2.B", "224.0.1.95:55545");
		props.put("UTDF.3.RANGE", "[FE-LK]");
		props.put("UTDF.3.A", "224.0.1.96:55546");
		props.put("UTDF.3.B", "224.0.1.97:55547");
		props.put("UTDF.4.RANGE", "[LL-PB]");
		props.put("UTDF.4.A", "224.0.1.98:55548");
		props.put("UTDF.4.B", "224.0.1.99:55549");
		props.put("UTDF.5.RANGE", "[PC-SP]");
		props.put("UTDF.5.A", "224.0.1.100:55550");
		props.put("UTDF.5.B", "224.0.1.101:55551");
		props.put("UTDF.6.RANGE", "[SQ-ZZ]");
		props.put("UTDF.6.A", "224.0.1.102:55552");
		props.put("UTDF.6.B", "224.0.1.103:55553");

		props.put("NASDAQ." + NORMALIZER, NasdaqNormalizer.class.getName());
		props.put("NASDAQ.1.RANGE", "[A-Z]");
		props.put("NASDAQ.1.A", "233.54.12.111:26477");
		props.put("NASDAQ.1.B", "233.86.230.111:26477");

		props.put("BX." + NORMALIZER, BxNormalizer.class.getName());
		props.put("BX.1.RANGE", "[A-Z]");
		props.put("BX.1.A", "233.54.12.40:25475");
		props.put("BX.1.B", "233.86.230.40:25475");

		props.put("PSX." + NORMALIZER, PsxNormalizer.class.getName());
		props.put("PSX.1.RANGE", "[A-Z]");
		props.put("PSX.1.A", "233.54.12.45:26477");
		props.put("PSX.1.B", "233.86.230.45:26477");

		props.put("ARCA.1.RANGE", "1:[A-Z]");
		props.put("ARCA.1.A", "224.0.59.76:11076");
		props.put("ARCA.1.B", "224.0.59.204:11204");
		props.put("ARCA.2.RANGE", "2:[A-Z]");
		props.put("ARCA.2.A", "224.0.59.77:11077");
		props.put("ARCA.2.B", "224.0.59.205:11205");
		props.put("ARCA.3.RANGE", "3:[A-Z]");
		props.put("ARCA.3.A", "224.0.59.78:11078");
		props.put("ARCA.3.B", "224.0.59.206:11206");
		props.put("ARCA.4.RANGE", "4:[A-Z]");
		props.put("ARCA.4.A", "224.0.59.79:11079");
		props.put("ARCA.4.B", "224.0.59.207:11207");

		props.put("BATS.1.RANGE", "A-AIZZZZ");
		props.put("BATS.1.A", "224.0.62.2:30001");
		props.put("BATS.1.B", "233.19.3.128:30001");
	}

	public static String getProperty(String... strings)
	{
		StringBuilder builder = new StringBuilder();
		for (String string : strings)
		{
			builder.append(string);
			builder.append('.');
		}
		builder.deleteCharAt(builder.length() - 1);
		String key = builder.toString();
		String value = props.get(key);
		return (value == null) ? null : value.trim();
	}

	public static Object getInstanceProperty(String... strings)
	{
		StringBuilder builder = new StringBuilder();
		for (String string : strings)
		{
			builder.append(string);
			builder.append('.');
		}
		builder.deleteCharAt(builder.length() - 1);
		String key = builder.toString();
		return instanceProps.get(key);
	}

	public static void putInstanceProperty(Object object, String... strings)
	{
		StringBuilder builder = new StringBuilder();
		for (String string : strings)
		{
			builder.append(string);
			builder.append('.');
		}
		builder.deleteCharAt(builder.length() - 1);
		String key = builder.toString();
		instanceProps.put(key, object);
	}

	public static Map<String, String> getAsMap(String propAsMap)
	{
		if (propAsMap == null) return null;
		Map<String, String> map = new HashMap<String, String>();
		String[] commaSplit = propAsMap.split(",");
		for (String commaItem : commaSplit)
		{
			String[] equalSplit = commaItem.split("=");
			String key = equalSplit[0];
			String value = equalSplit[1];
			map.put(key, value);
		}
		return map;
	}

	public static Set<String> getAsSet(String propAsSet)
	{
		if (propAsSet == null) return null;
		Set<String> set = new HashSet<String>();
		String[] commaSplit = propAsSet.split(",");
		for (String commaItem : commaSplit)
		{
			set.add(commaItem);
		}
		return set;
	}
}
