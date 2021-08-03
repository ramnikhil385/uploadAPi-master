package com.doc360.apibridge.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaDataDateValidator {
	private static Pattern pattern;
	private static Matcher matcher;
	private static Pattern pattern2;
	private static Matcher matcher2;

	private static final String DATE_PATTERN = "(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/((19|20)\\d\\d)";
	private static final String DATE_PATTERN2 = "(([12]\\d{3})-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataDateValidator.class);

												
	static {
		pattern = Pattern.compile(DATE_PATTERN);
		pattern2 = Pattern.compile(DATE_PATTERN2);
	}

	/**
	 * Validate date format with regular expression
	 * 
	 * @param date
	 *            date address for validation
	 * @return true valid date format, false invalid date format
	 */
	public static boolean validate(final String date) {

		matcher = pattern.matcher(date);
		matcher2 = pattern2.matcher(date);

		if (matcher.matches()) {

			matcher.reset();

			if (matcher.find()) {

				String day = matcher.group(1);
				String month = matcher.group(2);
				int year = Integer.parseInt(matcher.group(3));

				if (day.equals("31") && (month.equals("4") || month.equals("6") || month.equals("9")
						|| month.equals("11") || month.equals("04") || month.equals("06") || month.equals("09"))) {
					return false; // only 1,3,5,7,8,10,12 has 31 days
				} else if (month.equals("2") || month.equals("02")) {
					// leap year
					if (year % 4 == 0) {
						if (day.equals("30") || day.equals("31")) {
							return false;
						} else {
							return true;
						}
					} else {
						if (day.equals("29") || day.equals("30") || day.equals("31")) {
							return false;
						} else {
							return true;
						}
					}
				} else {
					return true;
				}
			} else {
				return false;
			}
		}else if(matcher2.matches()){ 
			matcher2.reset();
			if (matcher2.find()) {
			String day = matcher2.group(3);
			String month = matcher2.group(2);
			String year2 = matcher2.group(1);
			int year = Integer.parseInt(year2.substring(0, 4));
			
			if (day.equals("31") && (month.equals("4") || month.equals("6") || month.equals("9")
					|| month.equals("11") || month.equals("04") || month.equals("06") || month.equals("09"))) {
				return false; // only 1,3,5,7,8,10,12 has 31 days
			} else if (month.equals("2") || month.equals("02")) {
				// leap year
				if (year % 4 == 0) {
					if (day.equals("30") || day.equals("31")) {
						return false;
					} else {
						return true;
					}
				} else {
					if (day.equals("29") || day.equals("30") || day.equals("31")) {
						return false;
					} else {
						return true;
					}
				}
			} else {
				return true;
			}}else {
				return false;
			}
		} else {
			return false;
		}
		
	}
	
	
public String convertDateFromString(String date)  {
		
		try {
			Date convertedDate;
			String formattedDate = "";
			if(date.contains(":")) {
				int index = date.indexOf(":")-3;
				String date2 = date.substring(0, index).trim();
				String time = date.substring(index);
				if (date2.matches("\\d{4}\\-\\d{2}\\-\\d{2}")) {
					//convertedDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
					formattedDate = date2;
				} else if (date2.matches("\\d{5}")) {
					convertedDate = new SimpleDateFormat("yyDDD", java.util.Locale.getDefault()).parse(date2);
					// Added for sonar issue fix
					formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
							.format(convertedDate);
				} else if (date2.matches("\\d{2}\\/\\d{2}\\/\\d{4}")) {
					convertedDate = new SimpleDateFormat(IConstants.DF_MONTHH_DATE_YR,
							java.util.Locale.getDefault()).parse(date2);
					formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
							.format(convertedDate);
					// logger.info("Converting MM/dd/YYYY {} to {}", date, formattedDate);
				} else if (date2.matches("\\d{6}")) {
					convertedDate = new SimpleDateFormat("yyMMdd", java.util.Locale.getDefault()).parse(date2);
					formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
							.format(convertedDate);
					// logger.info("Converting yyMMdd {} to {}", date, formattedDate);

				}
				//F299998-US1796073
				else if (date2.matches("\\d{4}-\\d{2}-\\d{2}")){  /*yyyy-MM-dd */
					convertedDate = new SimpleDateFormat(IConstants.DF_HYPHENATED_YR_MONTH_DAY,
							java.util.Locale.getDefault()).parse(date2);
					formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
							.format(convertedDate);
				}else if (date2.matches("\\d{2}-\\d{2}-\\d{4}")){/*MM-dd-yyyy*/ 
					convertedDate = new SimpleDateFormat(IConstants.DF_HYPHENATED_MONTH_DAY_YR,
							java.util.Locale.getDefault()).parse(date2);
					formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
							.format(convertedDate);
				}else if(date2.matches("(JAN(UARY)?|FEB(RUARY)?|MAR(CH)?|APR(IL)?|MAY|JUN(E)?|JUL(Y)?|AUG(UST)?|SEP(TEMBER)?|OCT(OBER)?|NOV(EMBER)?|DEC(EMBER)?)\\s+\\d{1,2},\\s+\\d{4}")) {
				
					convertedDate = new SimpleDateFormat(IConstants.DF_MONTH_DATE_YR,
							java.util.Locale.getDefault()).parse(date2);
							formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
							.format(convertedDate);
					
				}/*else if (MetaDataDateValidator.validate(date2)){
					if(date2.contains("-")) {
					convertedDate = new SimpleDateFormat(IConstants.DF_HYPHENATED_MONTH_DAY_YR,
							java.util.Locale.getDefault()).parse(date2);
					formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
							.format(convertedDate);
					}else if(date2.contains("/")) {
						convertedDate = new SimpleDateFormat(IConstants.DF_MONTHH_DATE_YR,
								java.util.Locale.getDefault()).parse(date2);
						formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
								.format(convertedDate);
					}
				}*/
				formattedDate = formattedDate + time;
			}else {
			if (date.matches("\\d{4}\\-\\d{2}\\-\\d{2}")) {
				//convertedDate = new SimpleDateFormat("MM/dd/yyyy").parse(date);
				formattedDate = date;
			} else if (date.matches("\\d{5}")) {
				convertedDate = new SimpleDateFormat("yyDDD", java.util.Locale.getDefault()).parse(date);
				// Added for sonar issue fix
				formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
						.format(convertedDate);
			} else if (date.matches("\\d{2}\\/\\d{2}\\/\\d{4}")) {
				convertedDate = new SimpleDateFormat(IConstants.DF_MONTHH_DATE_YR,
						java.util.Locale.getDefault()).parse(date);
				formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
						.format(convertedDate);
				// logger.info("Converting MM/dd/YYYY {} to {}", date, formattedDate);
			} else if (date.matches("\\d{6}")) {
				convertedDate = new SimpleDateFormat("yyMMdd", java.util.Locale.getDefault()).parse(date);
				formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
						.format(convertedDate);
				// logger.info("Converting yyMMdd {} to {}", date, formattedDate);

			}
			//F299998-US1796073
			else if (date.matches("\\d{4}-\\d{2}-\\d{2}")){ /* yyyy-MM-dd */
				convertedDate = new SimpleDateFormat(IConstants.DF_HYPHENATED_YR_MONTH_DAY,
						java.util.Locale.getDefault()).parse(date);
				formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
						.format(convertedDate);
			}else if (date.matches("\\d{2}-\\d{2}-\\d{4}")){/*MM-dd-yyyy */
				convertedDate = new SimpleDateFormat(IConstants.DF_HYPHENATED_MONTH_DAY_YR,
						java.util.Locale.getDefault()).parse(date);
				formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
						.format(convertedDate);
			}else if(date.matches("(JAN(UARY)?|FEB(RUARY)?|MAR(CH)?|APR(IL)?|MAY|JUN(E)?|JUL(Y)?|AUG(UST)?|SEP(TEMBER)?|OCT(OBER)?|NOV(EMBER)?|DEC(EMBER)?)\\s+\\d{1,2},\\s+\\d{4}")) {
			
				convertedDate = new SimpleDateFormat(IConstants.DF_MONTH_DATE_YR,
						java.util.Locale.getDefault()).parse(date);
						formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
						.format(convertedDate);
				
			}}/*else if (MetaDataDateValidator.validate(date)){
				if(date.contains("-")) {
				convertedDate = new SimpleDateFormat(IConstants.DF_HYPHENATED_MONTH_DAY_YR,
						java.util.Locale.getDefault()).parse(date);
				formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
						.format(convertedDate);
				}else if(date.contains("/")) {
					convertedDate = new SimpleDateFormat(IConstants.DF_MONTHH_DATE_YR,
							java.util.Locale.getDefault()).parse(date);
					formattedDate = new SimpleDateFormat(IConstants.DATEFORMAT, java.util.Locale.getDefault())
							.format(convertedDate);
				}
			}*/
			return formattedDate;
		} catch (Exception e) {
			LOGGER.info("Exception occur while converting julian date {}", e);
			return "";
		}
	}
	
	public boolean validateDate(String date, boolean required, boolean validate) {
		String convertedDate = convertDateFromString(date);
		boolean isEmpty = StringUtils.isEmpty(convertedDate);
		boolean isNull = convertedDate == null;
		if((required || validate) && (isNull || isEmpty)) {
			return false;
		}
		return true;
	}

	
	
}
