//
//  MonopolySimulator.java
//  MonopolySimulator
//
//  Created by Craig McFarlane on 11/10/08.
//  Copyright (c) 2008 __MyCompanyName__. All rights reserved.
//

import java.lang.Math;
import java.util.EnumSet;
import java.util.*;


public class BoardGameSimulator {

	static final int kNumGames = 1;
	static final int kMinPlayers = 2;
	static final int kMaxPlayers = 9;
	static final int kBoardLocations = 40;
	static final int kDoublesLimit = 2;
	static final double kDefaultBankFloat = 100000.00;			// +=+ player floats should come out of bank float
	static final double kDefaultPlayerFloat = 1000.00;
	static final double kMortgageValue = 0.5;
	static final int kShuffleFactor = 3;
	static final int kNumHouses = 32;
	static final int kNumHotels = 12;
	
	static boolean fVerbose = true;
	static boolean fTrace = false;
	
	// ============================================================================

	
	public class TooManyHousesException extends Exception {
	}
	
	public class TooManyHotelsException extends Exception {
	}
	
	public class PropertyNotFoundException extends Exception {
	}
	
	public class PlayerNotFoundException extends Exception {
	}
	
	public class HotelNotFoundException extends Exception {
	}
	
	public class HouseNotFoundException extends Exception {
	}
	
	public class NotEnoughCashException extends Exception {
	}
	
	public class SiteCantImproveException extends Exception {
	}
	
	public class WrongPlayerException extends Exception {
	}
	
	public class BankBrokeException extends Exception {
	}
	
	public class NoMorePlayersException extends Exception {
	}
	
	// ============================================================================
	
	
	public enum PlayerName {
		Dick   ( "Dick"),
		Jane   ( "Jane"),
		Sally  ( "Sally"),
		Mother ( "Mother"),
		Father ( "Father"),
		Spot   ( "Spot"),
		Puff   ( "Puff"),
		Jack   ( "Jack"),
		Tim    ( "Tim");
		
		private String fName;
		
		private static Random fRandom = new Random();
		private static EnumSet<PlayerName> fSet = EnumSet.allOf( PlayerName.class);
		private static Object[] fObjs = fSet.toArray();
		private static Vector fPlayers = new Vector( fSet);
		private static boolean[] fUsed = new boolean[fSet.size()];
		private static int fNumAvailable = fSet.size();
		
		PlayerName( String qName) {
			if (fTrace) {
				System.out.println( "PlayerName.PlayerName( " + qName + ")");
			}
			fName = qName;
		}
		
		public String Name() {
			if (fTrace) {
				System.out.println( "PlayerName.Name()");
			}
			return fName;
		}
		
		public static void Reset() {
			int playerNo;
			
			if (fTrace) {
				System.out.println( "PlayerName.Reset()");
			}
			for ( playerNo = 0; playerNo < fSet.size(); playerNo++) {
				fUsed[playerNo] = false;
			}
			fNumAvailable = fSet.size();
		}
		
		public static int size() {
			return fSet.size();
		}
		
		private static PlayerName Random() {
			PlayerName ret;
			
			if (fTrace) {
				System.out.println( "PlayerName.Random()");
			}
			ret = (PlayerName) fObjs[fRandom.nextInt( fObjs.length)];
			return ret;
		}
		
		public static PlayerName UniqueRandom() {
			boolean done = false;
			PlayerName thePlayer = null;
			int playerNo;
			
			if (fTrace) {
				System.out.println( "PlayerName.UniqueRandom()");
			}
			if (fNumAvailable == 0) {
				return thePlayer;
			}
			while (!done) {
				thePlayer = Random();
				playerNo = fPlayers.indexOf( thePlayer);
				if (!fUsed[playerNo]) {
					fUsed[playerNo] = true;
					done = true;
					fNumAvailable--;
				}
			}
			return thePlayer;
		}
	}
	
	// ============================================================================

	
	public enum Piece {
		BATTLESHIP      ( "Battleship"),
		BOOT            ( "Boot"),
		CAR             ( "Car"),
		CANNON          ( "Cannon"),
		HORSE_AND_RIDER ( "Horse & Rider"),
		IRON            ( "Iron"),
		MONEYBAG        ( "Moneybag"),
		SCOTTIE_DOG     ( "Scottie Dog"),
		THIMBLE         ( "Thimble"),
		TOP_HAT         ( "Top Hat"),
		TRAIN           ( "Train"),
		WHEELBARROW     ( "Wheelbarrow");

		private String fName;
		
		private static Random fRandom = new Random();
		private static EnumSet<Piece> fSet = EnumSet.allOf( Piece.class);
		private static Object[] fObjs = fSet.toArray();
		private static Vector fPieces = new Vector( fSet);
		private static boolean[] fUsed = new boolean[fSet.size()];
		private static int fNumAvailable = fSet.size();
		
		Piece( String qName) {
			if (fTrace) {
				System.out.println( "Piece.Piece( " + qName + ")");
			}
			fName = qName;
		}
		
		public String Name() {
			if (fTrace) {
				System.out.println( "Piece.Name()");
			}
			return fName;
		}
		
		public static void Reset() {
			int pieceNo;
			
			if (fTrace) {
				System.out.println( "Piece.Reset()");
			}
			for ( pieceNo = 0; pieceNo < fSet.size(); pieceNo++) {
				fUsed[pieceNo] = false;
			}
			fNumAvailable = fSet.size();
		}
		
		public static int size() {
			return fSet.size();
		}
		
		private static Piece Random() {
			Piece ret;
			
			if (fTrace) {
				System.out.println( "Piece.Random()");
			}
			ret = (Piece) fObjs[fRandom.nextInt( fObjs.length)];
			return ret;
		}

		public static Piece UniqueRandom() {
			boolean done = false;
			Piece thePiece = null;
			int pieceNo;
			
			if (fTrace) {
				System.out.println( "Piece.UniqueRandom()");
			}
			if (fNumAvailable == 0) {
				return thePiece;
			}
			while (!done) {
				thePiece = Random();
				pieceNo = fPieces.indexOf( thePiece);
				if (!fUsed[pieceNo]) {
					fUsed[pieceNo] = true;
					done = true;
					fNumAvailable--;
				}
			}
			return thePiece;
		}
	}

	// ============================================================================

	
	private enum Country {
		USA;
		
		// +=+ how do we tie in the card sets and properties here?

		private static Random fRandom = new Random();
		private static EnumSet<Country> fSet = EnumSet.allOf( Country.class);
		private static Object[] fObjs = fSet.toArray();
		private static Iterator it = fSet.iterator();
		
		public static Country Random() {
			Country ret;
			
			if (fTrace) {
				System.out.println( "Country.Random()");
			}
			ret = (Country) fObjs[fRandom.nextInt( fObjs.length)];
			return ret;
		}
	}
	
	// ============================================================================

	
	public enum CDestination {
		kBackThreeLocation	( -3),
		kNoLocation			( -1),
		kFirstLocation		(  0),
		kLastLocation		( 39);
		private int fDestination;
		
		CDestination( int qDestination) {
			fDestination = qDestination;
		}
	}
	
	// ============================================================================

	
	public enum SpecialAction {
		None,
		CollectFromEveryPlayer,
		NearestUtility,
		NearestRailroad,
		GetOutOfJailFree,
		GoToJail,
		PropertyRepairs,
		IncomeTax,
		LuxuryTax,
		StreetRepairs;
	}
	
	// ============================================================================

	
	public enum CCardAction {
		
		// chance
		AdvanceToCharlesPlace		( "Advance to St Charles Place", 0.00, 11, SpecialAction.None),
		AdvanceToIllinoisPlace		( "Advance to Illinois Ave", 0.00, 24, SpecialAction.None),
		CAdvanceToGo				( "Advance to Go", 0.00, 0, SpecialAction.None),
		AdvanceToNearestUtility		( "Advance to Nearest Utility", 0.00, -1, SpecialAction.None),
		AdvanceToNearestRailroad	( "Advance to the nearest Railroad", 0.00, -1, SpecialAction.None),
		AdvanceToBoardwalk			( "Advance Token to Boardwalk", 0.00, 39, SpecialAction.None),
		BankPaysDividend			( "Bank Pays you Dividend of $50", 50.00, -1, SpecialAction.None),
		ElectedChairman				( "Elected Chairman of Board Pay $50", -50.00, -1, SpecialAction.None),
		CGetOutOfJailFree			( "Get Out of Jail Free", 0.00, -1, SpecialAction.GetOutOfJailFree),
		GoBack3Spaces				( "Go Bank 3 Spaces", 0.00, -3, SpecialAction.None),
		GoDirectlyToJail			( "Go Directly to Jail", 0.00, 10, SpecialAction.GoToJail),
		PropertyRepairs				( "Make General Repairs on Your Property $25/house  $100/Hotel", 0.00, -1, SpecialAction.PropertyRepairs),
		PayPoorTax					( "Pay Poor Tax of $15", -15.00, -1, SpecialAction.None),
		TakeARideOnTheReading		( "Take a ride on the Reading", 0.00, 5, SpecialAction.None),
		YourBuildingLoanMatures		( "Your Building & Loan Matures Collect $150", 150.00, -1, SpecialAction.None),
		
		// community chest
		CCAdvanceToGo				( "Advance To Go (Collect $200)", 200.00, 0, SpecialAction.None),
		BankError					( "Bank Error In Your Favor(Collect $200)", 200.00, -1, SpecialAction.None),
		CollectFromEveryPlayer		( "Collect $50 from Every Player for opening night seats", 0.00, -1, SpecialAction.CollectFromEveryPlayer),
		DoctorFee					( "Doctor’s Fee Pay $50", 50.00, -1, SpecialAction.None),
		StockSale					( "From Sale of Stock You Get $45", 45.00, -1, SpecialAction.None),
		CCGetOutOfJailFree			( "Get Out of Jail Free", 0.00, -1, SpecialAction.GetOutOfJailFree),
		GoToJail					( "Go to Jail", 0.00, -1, SpecialAction.GoToJail),
		IncomeTaxRefund				( "Income Tax Refund  Collect $20", 20.00, -1, SpecialAction.None),
		LifeInsuranceMatures		( "Life Insurance Matures Collect $100", 100.00, -1, SpecialAction.None),
		PayHospital					( "Pay Hospital $100", -100.00, -1, SpecialAction.None),
		PaySchoolTax				( "Pay School Tax of $150", 150.00, -1, SpecialAction.None),
		ReceiveForServices			( "Receive for Services $25", 25.00, -1, SpecialAction.None),
		SecondPrizeBeautyContest	( "Second Prize Beauty Contest Collect $10", 10.00, -1, SpecialAction.None),
		StreetRepairs				( "You are assessed for Street Repairs $40/house, $115/hotel", 0, -1, SpecialAction.StreetRepairs),
		Inheritance					( "You Inherit $100", 100.00, -1, SpecialAction.None),
		XmasFundMatures				( "Xmas Fund Matures, $100.00", 100.00, -1, SpecialAction.None);
		
		private final String fText;
		private final double fPrice;
		private final int fLocation;
		private SpecialAction fSpecialAction;
		
		CCardAction( String qText, double qPrice, int qLocation, SpecialAction qSpecialAction) {
			if (fTrace) {
				System.out.println( "CCardAction.CCardAction( " + qText + ")");
			}
			fText = qText;
			fPrice = qPrice;
			fLocation = qLocation;
			fSpecialAction = qSpecialAction;
		}
		
		public boolean AffectsLocation() {
			if (fTrace) {
				System.out.println( "CCardAction.AffectsLocation()");
			}
			if (fLocation >= 0) {
				return true;
			}
			return false;
		}
		
		public int ToLocation() {
			if (fTrace) {
				System.out.println( "CCardAction.ToLocation()");
			}
			return fLocation;
		}
		
		public boolean IsGetOutOfJailFree() {
			if (fTrace) {
				System.out.println( "CCardAction.IsGetOutOfJailFree()");
			}
			boolean ret = (fSpecialAction == SpecialAction.GetOutOfJailFree);
			return ret;
		}
	}
	
	// ============================================================================

	
	public enum CPropertyGroup {
		NONE		( "None", 0),
		VIOLET		( "Violet", 2),
		LIGHT_BLUE	( "LightBlue", 3),
		PURPLE		( "Purple", 3),
		ORANGE		( "Orange", 3),
		RED			( "Red", 3),
		YELLOW		( "Yellow", 3),
		GREEN		( "Green", 3),
		BLUE		( "Blue", 2),
		RAILROAD	( "Railroad", 4),
		UTILITY		( "Utility", 2);
		
		private final String fName;
		private final int fNumMembers;
		
		CPropertyGroup( String qName, int qNumMembers) {
			if (fTrace) {
				System.out.println( "CPropertyGroup.CPropertyGroup( " + qName + ")");
			}
			fName = qName;
			fNumMembers = qNumMembers;
		}
		
		public int NumMembers() {
			if (fTrace) {
				System.out.println( "CPropertyGroup.NumMembers()");
			}
			return fNumMembers;
		}
	}
	
	// ============================================================================

	
	public class CProperty {
		int fLocation;
		String fName;
		String fOther;
		CPropertyGroup fCPropertyGroup;
		double fPrice;
		double fHousePrice;
		boolean fImprovable;
		double fSiteRent;
		double fHouseRent[];
		double fHotelRent;
		boolean fSellable;
		CParticipant fOwner;
		boolean fMortgaged;
		int fNumHouses;
		boolean fHotel;
		SpecialAction fSpecialAction;
		
		CProperty( int qLocation, String qName, String qOther, CPropertyGroup qCPropertyGroup, boolean qImprovable, boolean qSellable, CParticipant qOwner) {
			if (fTrace) {
				System.out.println( "CProperty.CProperty( " + qName + ")");
			}
			fLocation = qLocation;
			fName = qName;
			fOther = qOther;
			fCPropertyGroup = qCPropertyGroup;
			fImprovable = qImprovable;
			fSellable = qSellable;
			fOwner = qOwner;
			
			fPrice = 0.00;
			fSiteRent = 0.00;
			fHouseRent = new double[4];
			fHouseRent[0] = 0.00;
			fHouseRent[1] = 0.00;
			fHouseRent[2] = 0.00;
			fHouseRent[3] = 0.00;
			fHotelRent = 0.00;
			fMortgaged = false;
			fNumHouses = 0;
			fHotel = false;
			fSpecialAction = SpecialAction.None;
		}
		
		public void SetPrices( double qPrice, double qHousePrice) {
			if (fTrace) {
				System.out.println( "CProperty.SetPrices()");
			}
			fPrice = qPrice;
			fHousePrice = qHousePrice;
		}
		
		public void SetRents( double qSite, double q1House, double q2House, double q3House, double q4House, double qHotel) {
			if (fTrace) {
				System.out.println( "CProperty.SetRents()");
			}
			fSiteRent = qSite;
			fHouseRent[0] = q1House;
			fHouseRent[1] = q2House;
			fHouseRent[2] = q3House;
			fHouseRent[3] = q4House;
			fHotelRent = qHotel;
		}
		
		public String Name() {
			if (fTrace) {
				System.out.println( "CProperty.Name()");
			}
			return fName;
		}
		
		public void SetSpecialAction( SpecialAction qSpecialAction) {
			if (fTrace) {
				System.out.println( "CProperty.SetSpecialAction()");
			}
			fSpecialAction = qSpecialAction;
		}
		
		public SpecialAction SpecialAction() {
			if (fTrace) {
				System.out.println( "CProperty.SpecialAction()");
			}
			return fSpecialAction;
		}
		
		public CPropertyGroup Color() {
			if (fTrace) {
				System.out.println( "CProperty.Color()");
			}
			return fCPropertyGroup;
		}
		
		public boolean IsMortgaged() {
			if (fTrace) {
				System.out.println( "CProperty.IsMortgaged()");
			}
			return true;
		}
		
		public double MortgageValue() {
			double ret = 0.00;
			
			if (fTrace) {
				System.out.println( "CProperty.MortgageValue()");
			}
			// +=+ figure out mortage value of property, including houses & hotels
			return ret;
		}
		
		public boolean IsSellable() {
			if (fTrace) {
				System.out.println( "CProperty.IsSellable()");
			}
			return fSellable;
		}
		
		public void Mortgage() throws NotEnoughCashException {
			double mortgageValue;
			
			if (fTrace) {
				System.out.println( "CProperty.Mortgage()");
			}
			fMortgaged = true;
			mortgageValue = fPrice * kMortgageValue;
			fOwner.AddFunds( mortgageValue);
		}
		
		public void Buy( CParticipant qOwner) {
			if (fTrace) {
				System.out.println( "CProperty.Buy()");
			}
			fOwner = qOwner;
		}
		
		public CParticipant Owner() {
			if (fTrace) {
				System.out.println( "CProperty.Owner()");
			}
			return fOwner;
		}
		
		public double Price() {
			if (fTrace) {
				System.out.println( "CProperty.Price()");
			}
			return fPrice;
		}
		
		public double HousePrice() {
			if (fTrace) {
				System.out.println( "CProperty.HousePrice()");
			}
			return fHousePrice;
		}
		
		public int NumHouses() {
			if (fTrace) {
				System.out.println( "CProperty.NumHouses()");
			}
			return fNumHouses;
		}
		
		public void BuildHouse() throws TooManyHousesException {
			if (fTrace) {
				System.out.println( "CProperty.BuildHouse()");
			}
			if (fNumHouses < 4) {
				fNumHouses++;
				// +=+ give money to bank
			} else {
				throw new TooManyHousesException();
			}
		}
		
		public double HotelPrice() {
			if (fTrace) {
				System.out.println( "CProperty.HotelPrice()");
			}
			// +=+ get price of hotel
			return 0.00;
		}
		
		public void BuildHotel() {
			if (fTrace) {
				System.out.println( "CProperty.BuildHotel()");
			}
			fNumHouses = 0;
			// +=+ return houses to bank
			fHotel = true;
		}
		
		public boolean HasHotel() {
			if (fTrace) {
				System.out.println( "CProperty.HasHotel()");
			}
			return fHotel;
		}
		
		public double Rent() throws TooManyHousesException {
			double ret = 0.00;
			
			if (fTrace) {
				System.out.println( "CProperty.Rent()");
			}
			if (!fMortgaged) {
				if (fHotel) {
					ret = fHotelRent;
				} else {
					switch (fNumHouses) {
						case 0:
							ret = fSiteRent;
							break;
						case 1:
							ret = fHouseRent[0];
							break;
						case 2:
							ret = fHouseRent[1];
							break;
						case 3:
							ret = fHouseRent[2];
							break;
						case 4:
							ret = fHouseRent[3];
							break;
						default:
							throw new TooManyHousesException();
					}
				}
			}
			return ret;
		}
		
	}
	
	// ============================================================================
	
	
	public class CBoard {
		Vector fProperties;
		CParticipant fBank;
		Country fCountry;
		
		CBoard( Country qCountry, CParticipant qBank) {
			if (fTrace) {
				System.out.println( "CBoard.CBoard()");
			}
			fCountry = qCountry;
			fProperties = new Vector();
			fBank = qBank;
			switch (qCountry) {
				case USA:
					AddLocations_USA();
					break;
			}
		}
		
		public CProperty GetProperty( int qLocation) {
			if (fTrace) {
				System.out.println( "CBoard.GetProperty()");
			}
			return (CProperty) fProperties.elementAt( qLocation);
		}
		
		private void AddLocations_USA() {
			CProperty newProperty;
			
			if (fTrace) {
				System.out.println( "CBoard.AddLocations_USA()");
			}

			// side 1
			newProperty = new CProperty( 0, "Go", "Collect $200.00 Salary As You Pass", CPropertyGroup.NONE, false, false, fBank);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 1, "Mediter Banlan Avenue", "", CPropertyGroup.VIOLET, true, true, fBank);
			newProperty.SetPrices( 60.00, 50.00);
			newProperty.SetRents( 2.00, 10.00, 30.00, 90.00, 160.00, 250.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 2, "Community Chest", "Follow Instructions On Top Card", CPropertyGroup.NONE, false, false, fBank);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 3, "Baltic Avenue", "", CPropertyGroup.VIOLET, true, true, fBank);
			newProperty.SetPrices( 60.00, 50.00);
			newProperty.SetRents( 4.00, 20.00, 60.00, 180.00, 320.00, 450.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 4, "Income Tax", "Pay 10% Or $200", CPropertyGroup.NONE, false, false, fBank);
			newProperty.SetSpecialAction( SpecialAction.IncomeTax);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 5, "Reading Railroad", "", CPropertyGroup.RAILROAD, false, true, fBank);
			newProperty.SetPrices( 200.00, 0.00);
			newProperty.SetRents( 50.00, 0.00, 0.00, 0.00, 0.00, 0.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 6, "Oriental Avenue", "", CPropertyGroup.LIGHT_BLUE, true, true, fBank);
			newProperty.SetPrices( 100.00, 50.00);
			newProperty.SetRents( 6.00, 30.00, 90.00, 270.00, 400.00, 550.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 7, "Chance", "", CPropertyGroup.NONE, false, false, fBank);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 8, "Vermont Avenue", "", CPropertyGroup.LIGHT_BLUE, true, true, fBank);
			newProperty.SetPrices( 100.00, 50.00);
			newProperty.SetRents( 6.00, 30.00, 90.00, 270.00, 400.00, 550.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 9, "Conneticut Avenue", "", CPropertyGroup.LIGHT_BLUE, true, true, fBank);
			newProperty.SetPrices( 120.00, 50.00);
			newProperty.SetRents( 8.00, 40.00, 100.00, 300.00, 450.00, 600.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			
			// side 2
			newProperty = new CProperty( 10, "In Jail", "Just Visiting", CPropertyGroup.NONE, false, false, fBank);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 11, "St.Charles Place", "", CPropertyGroup.PURPLE, true, true, fBank);
			newProperty.SetPrices( 140.00, 100.00);
			newProperty.SetRents( 10.00, 50.00, 150.00, 450.00, 625.00, 750.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 12, "Electric Company", "", CPropertyGroup.UTILITY, false, true, fBank);
			newProperty.SetPrices( 150.00, 0.00);
			newProperty.SetRents( 50.00, 0.00, 0.00, 0.00, 0.00, 0.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 13, "States Avenue", "", CPropertyGroup.PURPLE, true, true, fBank);
			newProperty.SetPrices( 140.00, 100.00);
			newProperty.SetRents( 10.00, 50.00, 150.00, 450.00, 625.00, 750.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 14, "Virginia Avenue", "", CPropertyGroup.PURPLE, true, true, fBank);
			newProperty.SetPrices( 160.00, 100.00);
			newProperty.SetRents( 12.00, 60.00, 180.00, 500.00, 700.00, 900.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 15, "Pennsylvania Railroad", "", CPropertyGroup.RAILROAD, false, true, fBank);
			newProperty.SetPrices( 200.00, 0.00);
			newProperty.SetRents( 50.00, 0.00, 0.00, 0.00, 0.00, 0.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 16, "St.James Place", "", CPropertyGroup.ORANGE, true, true, fBank);
			newProperty.SetPrices( 180.00, 100.00);
			newProperty.SetRents( 14.00, 70.00, 200.00, 550.00, 750.00, 950.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 17, "Community Chest", "Follow Instructions On Top Card", CPropertyGroup.NONE, false, false, fBank);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 18, "Tenessee Avenue", "", CPropertyGroup.ORANGE, true, true, fBank);
			newProperty.SetPrices( 180.00, 100.00);
			newProperty.SetRents( 14.00, 70.00, 200.00, 550.00, 750.00, 950.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 19, "New York Avenue", "", CPropertyGroup.ORANGE, true, true, fBank);
			newProperty.SetPrices( 200.00, 100.00);
			newProperty.SetRents( 16.00, 80.00, 220.00, 600.00, 800.00, 1000.00);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			
			// side 3
			newProperty = new CProperty( 20, "Just Parking", "", CPropertyGroup.NONE, false, false, fBank);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 21, "Kentucky Avenue", "", CPropertyGroup.RED, true, true, fBank);
			newProperty.SetPrices( 220.00, 150.00);
			newProperty.SetRents( 18.00, 90.00, 250.00, 700.00, 875.00, 1050.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 22, "Chance", "", CPropertyGroup.NONE, false, false, fBank);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 23, "Indiana Avenue", "", CPropertyGroup.RED, true, true, fBank);
			newProperty.SetPrices( 220.00, 150.00);
			newProperty.SetRents( 18.00, 90.00, 250.00, 700.00, 875.00, 1050.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 24, "Illonois Avenue", "", CPropertyGroup.RED, true, true, fBank);
			newProperty.SetPrices( 240.00, 150.00);
			newProperty.SetRents( 20.00, 100.00, 300.00, 750.00, 925.00, 1100.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 25, "B&O Railroad", "", CPropertyGroup.RAILROAD, false, true, fBank);
			newProperty.SetPrices( 200.00, 0.00);
			newProperty.SetRents( 50.00, 0.00, 0.00, 0.00, 0.00, 0.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 26, "Atlantic Avenue", "", CPropertyGroup.YELLOW, true, true, fBank);
			newProperty.SetPrices( 260.00, 150.00);
			newProperty.SetRents( 22.00, 110.00, 330.00, 800.00, 975.00, 1150.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 27, "Ventnor Square", "", CPropertyGroup.YELLOW, true, true, fBank);
			newProperty.SetPrices( 260.00, 150.00);
			newProperty.SetRents( 22.00, 110.00, 330.00, 800.00, 975.00, 1150.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 28, "Water Works", "", CPropertyGroup.UTILITY, false, true, fBank);
			newProperty.SetPrices( 150.00, 0.00);
			newProperty.SetRents( 50.00, 0.00, 0.00, 0.00, 0.00, 0.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 29, "Marvin Gardens", "", CPropertyGroup.YELLOW, true, true, fBank);
			newProperty.SetPrices( 280.00, 150.00);
			newProperty.SetRents( 24.00, 120.00, 360.00, 850.00, 1025.00, 1200.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			
			// side 4
			newProperty = new CProperty( 30, "Go To Jail", "", CPropertyGroup.NONE, false, false, fBank);
			newProperty.SetSpecialAction( SpecialAction.GoToJail);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 31, "Pacific Avenue", "", CPropertyGroup.GREEN, true, true, fBank);
			newProperty.SetPrices( 300.00, 200.00);
			newProperty.SetRents( 26.00, 130.00, 390.00, 900.00, 1100.00, 1275.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 32, "North Carolina Avenue", "", CPropertyGroup.GREEN, true, true, fBank);
			newProperty.SetPrices( 300.00, 200.00);
			newProperty.SetRents( 26.00, 130.00, 390.00, 900.00, 1100.00, 1275.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 33, "Community Chest", "Follow Instructions On Top Card", CPropertyGroup.NONE, false, false, fBank);
			fProperties.add( newProperty); 
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 34, "Pennsylvania Avenue", "", CPropertyGroup.GREEN, true, true, fBank);
			newProperty.SetPrices( 320.00, 200.00);
			newProperty.SetRents( 28.00, 150.00, 450.00, 1000.00, 1200.00, 1400.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 35, "Short Line", "", CPropertyGroup.RAILROAD, false, true, fBank);
			newProperty.SetPrices( 200.00, 0.00);
			newProperty.SetRents( 50.00, 0.00, 0.00, 0.00, 0.00, 0.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 36, "Chance", "", CPropertyGroup.NONE, false, false, fBank);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 37, "Park Place", "", CPropertyGroup.BLUE, true, true, fBank);
			newProperty.SetPrices( 350.00, 200.00);
			newProperty.SetRents( 35.00, 175.00, 500.00, 1100.00, 1300.00, 1500.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 38, "Luxury Tax", "", CPropertyGroup.NONE, false, false, fBank);
			newProperty.SetSpecialAction( SpecialAction.LuxuryTax);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
			newProperty = new CProperty( 39, "Boardwalk", "", CPropertyGroup.BLUE, true, true, fBank);
			newProperty.SetPrices( 400.00, 200.00);
			newProperty.SetRents( 50.00, 200.00, 600.00, 1400.00, 1700.00, 2000.00);
			fProperties.add( newProperty);
			fBank.AddProperty( newProperty);
		}
		
	}
	
	// ============================================================================
	
	
	public class CCard {
		CCardAction fAction;
		
		CCard( CCardAction qAction) {
			if (fTrace) {
				System.out.println( "CCard.CCard()");
			}
			fAction = qAction;
		}
		
		boolean IsGetOutOfJailFree() {
			if (fTrace) {
				System.out.println( "CCard.IsGetOutOfJailFree()");
			}
			boolean ret = fAction.IsGetOutOfJailFree();
			return ret;
		}
	}
	
	// ============================================================================
	
	
	public class CDeck {
		String fName;
		Vector fFresh;
		Vector fUsed;
		
		CDeck( String qName) {
			if (fTrace) {
				System.out.println( "CDeck.CDeck( " + qName + ")");
			}
			fName = qName;
			fFresh = new Vector();
			fUsed = new Vector();
		}
		
		private void AddCard( CCard qCard) {
			if (fTrace) {
				System.out.println( "CDeck.AddCard()");
			}
			fFresh.add( qCard);
		}
		
		private void Shuffle() {
			int swapNo;
			int numSwaps;
			Random theRandom;
			
			if (fTrace) {
				System.out.println( "CDeck.Shuffle()");
			}
			// +=+ move cards from used to fresh
			theRandom = new Random();
			// shuffle individual cards
			numSwaps = kShuffleFactor * fFresh.size();
			for ( swapNo = 0; swapNo < numSwaps; swapNo++) {
				CCard swapCard;
				int index1, index2;
				
				index1 = theRandom.nextInt( fFresh.size());
				index2 = theRandom.nextInt( fFresh.size());
				swapCard = (CCard) fFresh.elementAt( index1);
				fFresh.setElementAt( fFresh.elementAt( index2), index1);
				fFresh.setElementAt( swapCard, index2);
			}
		}
		
		/**
		 * draws a Card from the deck
		 *
		 * @return      the drawn card
		 * @see         Card
		 */
		public CCard Draw() {
			CCard ret;
			if (fTrace) {
				System.out.println( "CDeck.Draw()");
			}
			if (fFresh.size() == 0) {
				Shuffle();
			}
			ret = (CCard) fFresh.elementAt( 0);
			fFresh.remove( 0);
			if (!ret.IsGetOutOfJailFree()) {
				// if Get Out Of Jail card, then give to player until they use it.
				fUsed.add( ret);
			}
			
			return ret;
		}
	}
	
	// ============================================================================
	
	
	public class CChanceDeck extends CDeck {
		CChanceDeck() {
			super( "Chance");
			if (fTrace) {
				System.out.println( "CChanceDeck.CChanceDeck()");
			}
			MakeDeck();
			super.Shuffle();
		}
		
		public void MakeDeck() {
			CCard newCard;
			
			if (fTrace) {
				System.out.println( "CChanceDeck.CCommunityChestDeck()");
			}
			newCard = new CCard( CCardAction.AdvanceToCharlesPlace);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.AdvanceToIllinoisPlace);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.CAdvanceToGo);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.AdvanceToNearestUtility);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.AdvanceToNearestRailroad);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.AdvanceToBoardwalk);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.BankPaysDividend);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.ElectedChairman);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.CGetOutOfJailFree);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.GoBack3Spaces);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.GoDirectlyToJail);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.PropertyRepairs);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.PayPoorTax);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.TakeARideOnTheReading);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.YourBuildingLoanMatures);
			super.AddCard( newCard);
		}
	}
	
	// ============================================================================
	
	
	public class CCommunityChestDeck extends CDeck {
		CCommunityChestDeck() {
			super( "Community Chest");
			if (fTrace) {
				System.out.println( "CCommunityChestDeck.CCommunityChestDeck()");
			}
			MakeDeck();
			super.Shuffle();
		}
		
		public void MakeDeck() {
			CCard newCard;
			
			if (fTrace) {
				System.out.println( "CCommunityChestDeck.MakeDeck()");
			}
			newCard = new CCard( CCardAction.CCAdvanceToGo);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.BankError);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.CollectFromEveryPlayer);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.DoctorFee);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.StockSale);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.CCGetOutOfJailFree);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.GoToJail);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.IncomeTaxRefund);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.LifeInsuranceMatures);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.PayHospital);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.PaySchoolTax);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.ReceiveForServices);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.SecondPrizeBeautyContest);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.StreetRepairs);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.Inheritance);
			super.AddCard( newCard);
			newCard = new CCard( CCardAction.XmasFundMatures);
			super.AddCard( newCard);
		}
	}
	
	// ============================================================================
	
	
	public class CParticipant {
		String fName;
		double fFunds;
		double fEarnt;
		double fSpent;
		Vector fProperties;
		CBoard fBoard;
		CBank fBank;
		Vector fCards;
		int fNumTurns;
		
		CParticipant( String qName, double qFloat) {
			if (fTrace) {
				System.out.println( "CParticipant.CParticipant( " + qName + ")");
			}
			fProperties = new Vector();
			fCards = new Vector();
			fName = qName;
			fFunds = qFloat;
		}
		
		public void SetBoard( CBoard qBoard) {
			if (fTrace) {
				System.out.println( "CParticipant.SetBoard()");
			}
			fBoard = qBoard;
		}
		
		public void SetBank( CBank qBank) {
			if (fTrace) {
				System.out.println( "CParticipant.SetBank()");
			}
			fBank = qBank;
		}
		
		public String Name() {
			if (fTrace) {
				System.out.println( "CParticipant.Name()");
			}
			return fName;
		}
		
		public void AddProperty( CProperty qProperty) {
			if (fTrace) {
				System.out.println( "CParticipant.AddProperty( " + qProperty.Name() + ")");
			}
			fProperties.add( qProperty);
		}
		
		public boolean OwnsProperty( CProperty qProperty) {
			boolean ret = false;
			
			if (fTrace) {
				System.out.println( "CParticipant.OwnsProperty()");
			}
			ret = fProperties.contains( qProperty);
			return ret;
		}
		
		public void AddCard( CCard qCard) {
			if (fTrace) {
				System.out.println( "CParticipant.AddCard()");
			}
			fCards.add( qCard);
		}
		
		public boolean HasFunds( double qAmount) {
			boolean ret = false;
			
			if (fTrace) {
				System.out.println( "CParticipant.HasFunds()");
			}
			if (fFunds >= qAmount) {
				ret = true;
			}
			return ret;
		}
		
		public void RaiseFunds( double qAmount) {
			// +=+ do what we need to do to make this amount
			// +=+ mortgage property?
			// +=+ sell hotel/house?
			// +=+ sell property? - to highest bidder?
			if (fTrace) {
				System.out.println( "CParticipant.RaiseFunds()");
			}
		}
		
		public void AddFunds( double qAmount) throws NotEnoughCashException {
			if (fTrace) {
				System.out.println( "CParticipant.AddFunds()");
			}
			if (qAmount >= 0) {
				fEarnt += qAmount;
			} else {
				fSpent += qAmount;
			}
			if (fFunds < qAmount) {
				throw new NotEnoughCashException();
			}
			fFunds += qAmount;
		}
		
		public void TakeTurn( int qTurnNo) throws BankBrokeException, NotEnoughCashException, PropertyNotFoundException, TooManyHousesException {
			fNumTurns = qTurnNo;
		}
		
		public void DoStatement( String qContext) {
			CProperty thisProperty;
			double totalWealth = TotalWealth();
			
			System.out.println( "----- start statement ----");
			System.out.print( fName + " " + qContext + " fNumTurns=");
			System.out.println( fNumTurns);
			System.out.print( "fFunds=$");
			System.out.println( fFunds);
			if (!fProperties.isEmpty()) {
				System.out.println( "Properties:");
				for (Iterator it = fProperties.iterator(); it.hasNext();) {
					thisProperty = (CProperty) it.next();
					System.out.println( thisProperty.Name());
				}
			}
			System.out.print( "total wealth=$");
			System.out.println( totalWealth);
			System.out.println( "------ end statement -----");
		}
		
		public double TotalWealth() {
			double totalWealth;
			double mortgageValue = 0.00;
			CProperty thisProperty;
			
			if (fTrace) {
				System.out.println( "CParticipant.TotalWealth()");
			}
			totalWealth = fFunds;
			if (!fProperties.isEmpty()) {
				for (Iterator it = fProperties.iterator(); it.hasNext();) {
					thisProperty = (CProperty) it.next();
					if (!thisProperty.IsMortgaged()) {
						mortgageValue += thisProperty.MortgageValue();		// +=+ method should be smart enough to deal with houses & hotels
					}
				}
			}
			totalWealth = fFunds + mortgageValue;
			return totalWealth;
		}
	}
	
	// ============================================================================
	
	
	public class CPlayer extends CParticipant {
		Piece fPiece;
		int fLocation;
		boolean fInJail;
		int fConsecutiveDoubles;
		boolean fBankrupt;
		CDice fDice;
		
		CPlayer( String qName, double qFloat, Piece qPiece, int qStartLocation) {
			super( qName, qFloat);
			if (fTrace) {
				System.out.println( "CPlayer.CPlayer()");
			}
			fPiece = qPiece;
			fLocation = qStartLocation;
			fInJail = false;
			fBankrupt = false;
			fNumTurns = 0;
		}
		
		public void SetDice( CDice qDice) {
			fDice = qDice;
		}
		
		public Piece GetPiece() {
			if (fTrace) {
				System.out.println( "CPlayer.GetPiece()");
			}
			return fPiece;
		}
		
		public int GetLocation() {
			if (fTrace) {
				System.out.println( "CPlayer( " + fName + ").GetLocation()");
			}
			return fLocation;
		}
		
		public boolean IsBankrupt() {
			if (fTrace) {
				System.out.println( "CPlayer( " + fName + ").IsBankrupt()");
			}
			return fBankrupt;
		}
		
		public void TakeTurn( int qTurnNo) throws BankBrokeException, NotEnoughCashException, PropertyNotFoundException, TooManyHousesException {
			int rollTotal;
			boolean doublesFlag = true;
			
			super.TakeTurn( qTurnNo);
			DoStatement( "Before");
			if (fTrace) {
				System.out.print( "CPlayer( " + fName + ").TakeTurn( ");
				System.out.print( qTurnNo);
				System.out.println( ")");
			}
			while (doublesFlag) {
				// keep taking turns while rolling doubles
				fDice.Roll();
				doublesFlag = fDice.IsDoubles();
				rollTotal = fDice.Total();
				System.out.print( fName + " rolls ");
				System.out.print( rollTotal);
				if (doublesFlag) {
					System.out.print( " - doubles");
				}
				System.out.println( "");
				Move( rollTotal, doublesFlag);
				if (fInJail) {
					break;
				}
				Act();
			}
			DoStatement( "After");
		}
		
		/**
		 * Moves the player to a new board position.
		 *
		 * @param  qDouble  true => doubles were rolled, else false
		 * @param  qValue the combined value of the roll
		 */
		public void Move( int qRollTotal, boolean qDoublesFlag) throws BankBrokeException, NotEnoughCashException {
			int newLocation = fLocation;
			
			if (fTrace) {
				System.out.println( "CPlayer( " + fName + ").Move()");
			}
			if (fVerbose) {
				System.out.print( fName + " moving from ");
				System.out.print( fLocation);
			}
			if (fInJail) {
				if (qDoublesFlag) {
					fInJail = false;
				}
			} else {
				if (qDoublesFlag) {
					fConsecutiveDoubles++;
				} else {
					fConsecutiveDoubles = 0;
				}
				if (fConsecutiveDoubles > kDoublesLimit) {
					fInJail = true;
					newLocation = 10;		// +=+ get jail location from board
				}
			}
			if (fInJail) {
				if (fVerbose) {
					fLocation = newLocation;
					System.out.print( " to ");
					System.out.println( fLocation);
				}
			} else {
				// move player's location by qValue
				newLocation += qRollTotal;
				fLocation = newLocation % kBoardLocations;
				if (fVerbose) {
					System.out.print( " to ");
					System.out.println( fLocation);
				}
				if (newLocation >= kBoardLocations) {
					// if we clock the board, collect $200
					fBank.Pay( this, 200.00);		// +=+ get $200 from go property?
					if (fVerbose) {
						System.out.print( fName + " passes Go, collects $");
						System.out.println( 200.00);
					}
				}
			}
		}
		
		/**
		 * Decides what to do, now player is at new location
		 *
		 */
		public void Act() throws TooManyHousesException, PropertyNotFoundException, NotEnoughCashException {
			CProperty thisProperty;
			thisProperty = fBoard.GetProperty( fLocation);
			SpecialAction theAction;
			
			if (fTrace) {
				System.out.println( "CPlayer( " + fName + ").Act()");
			}
			if (fVerbose) {
				System.out.println( fName + " visiting " + thisProperty.Name());
			}
			// first discover immediate liabilities
			if (thisProperty.Owner() == this) {
				// visiting our own property
				if (fVerbose) {
					System.out.println( fName + " visiting own property");
				}
			} else {
				if (thisProperty.IsSellable()) {
					if (fBank.OwnsProperty( thisProperty)) {
						// bank owns property
						if (fVerbose) {
							System.out.println( fName + " can buy this property");
						}
						if (ShouldBuyProperty( thisProperty)) {
							// buy property?
							fBank.SellProperty( thisProperty, this);
							if (fVerbose) {
								System.out.print( fName + " has bought " + thisProperty.Name() + " for $");
								System.out.println( thisProperty.Price());
							}
						}
					} else {
						CParticipant theOwner;
						double theRent = 0.00;
						// oh dear, someone elses property?
						
						theRent = thisProperty.Rent();
						theOwner = thisProperty.Owner();
						if (fVerbose) {
							System.out.println( fName + " owes " + theOwner.Name() + " rent");
							System.out.print( "Rent=$");
							System.out.println( theRent);
						}
						if (HasFunds( theRent)) {
							AddFunds( -theRent);
							theOwner.AddFunds( theRent);
						}
					}
				} else {
					// probably a special location
					if (fVerbose) {
						System.out.println( fName + " is on a special property");
					}
					theAction = thisProperty.SpecialAction();
				}
			}
			// improve property?
			// settle new debts?
			// how? cash? property sale?  mortgage?  bankruptcy?
			// choose action card?
			// redeem mortgage?
		}
		
		public boolean ShouldBuyProperty( CProperty qProperty) {
			boolean ret = false;
			
			if (HasFunds( qProperty.Price())) {
				ret = true;
			}
			return ret;
		}
		
		public boolean ConsiderOffer() {
			boolean ret = false;
			// +=+ player to player offers
			// +=+ HTF do we do that?
			if (fTrace) {
				System.out.println( "CPlayer.ConsiderOffer()");
			}
			return ret;
		}
		
		public void ApplyCard( CCard qCard) {
			// get action from card, decide what to do.
			// keep card if possible - out of jail
			if (fTrace) {
				System.out.println( "CPlayer.ApplyCard()");
			}
		}
	}
	
	public class CAggressivePlayer extends CPlayer {
		// +=+ make subclass for each player, with different strategies
		CAggressivePlayer( String qName, double qFloat, Piece qPiece, int qStartLocation) {
			super( qName, qFloat, qPiece, qStartLocation);
			if (fTrace) {
				System.out.println( "CAggressivePlayer.CAggressivePlayer()");
			}
		}

		public boolean ShouldBuyProperty( CProperty qProperty) {
			boolean ret = false;
			
			// +=+ as long as we've got enough cash, buy buy buy!!!
			return ret;
		}
	}
	
	public class CCautiousPlayer extends CPlayer {
		// +=+ make subclass for each player, with different strategies
		CCautiousPlayer( String qName, double qFloat, Piece qPiece, int qStartLocation) {
			super( qName, qFloat, qPiece, qStartLocation);
			if (fTrace) {
				System.out.println( "CCautiousPlayer.CCautiousPlayer()");
			}
		}

		public boolean ShouldBuyProperty( CProperty qProperty) {
			boolean ret = false;
			
			// +=+ keep cash reserves high - never spend more than 1/3 of our cash
			return ret;
		}
	}
	
	// ============================================================================
	
	
	public class CBank extends CParticipant {
		int fNumHouses;
		int fNumHotels;
		
		/**
		 * creates a bank
		 *
		 * @param  qFloat  the starting amount of cash in the bank
		 */
		CBank( double qFloat) {
			super( "Bank", qFloat);
			if (fTrace) {
				System.out.print( "CBank.CBank( ");
				System.out.print( qFloat);
				System.out.println( ")");
			}
			fNumHouses = kNumHouses;
			fNumHotels = kNumHotels;
		}
		
		public void Pay( CPlayer qPlayer, double qAmount) throws BankBrokeException, NotEnoughCashException {
			if (fTrace) {
				System.out.print( "CBank.Pay( " + qPlayer.Name() + ", ");
				System.out.print( qAmount);
				System.out.println( ")");
			}
			if (fFunds < qAmount) {
				throw new BankBrokeException();
			}
			qPlayer.AddFunds( qAmount);
			AddFunds( -qAmount);
		}
		
		/**
		 * Sells a house for the specified property to the specified player
		 *
		 * @param  qProperty  the property being improved
		 * @param  qPlayer  the player buying the house
		 */
		public boolean SellHouse( CProperty qProperty, CPlayer qPlayer) throws HouseNotFoundException, TooManyHousesException, NotEnoughCashException, SiteCantImproveException {
			boolean ret = false;
			double housePrice;
			
			if (fTrace) {
				System.out.println( "CBank.SellHouse( " + qProperty.Name() + ", " + qPlayer.Name() + ")");
			}
			if (fNumHouses == 0) {
				throw new HouseNotFoundException();
			}
			housePrice = qProperty.HousePrice();
			if (housePrice == 0.00) {
				throw new SiteCantImproveException();
			}
			// +=+ does player own all properties in color group?
			
			if (qProperty.NumHouses() >= 4) {
				throw new TooManyHousesException();
			}
			if (qPlayer.HasFunds( housePrice)) {
				fNumHouses--;
				qProperty.BuildHouse();
				qPlayer.AddFunds( -housePrice);
				AddFunds( housePrice);
				ret = true;
			}
			return ret;
		}
		
		/**
		 * Sells a hotel for the specified property to the specified player
		 *
		 * @param  qProperty  the property being improved
		 * @param  qPlayer  the player buying the house
		 */
		public boolean SellHotel( CProperty qProperty, CPlayer qPlayer) throws
		HotelNotFoundException, TooManyHotelsException, NotEnoughCashException, SiteCantImproveException, WrongPlayerException {
			boolean ret = false;
			double hotelPrice;
			
			if (fTrace) {
				System.out.println( "CBank.SellHotel( " + qProperty.Name() + ", " + qPlayer.Name() + ")");
			}
			if (qProperty.Owner() != qPlayer) {
				throw new WrongPlayerException();
			}
			if (fNumHotels == 0) {
				throw new HotelNotFoundException();
			}
			if (qProperty.HasHotel()) {
				throw new TooManyHotelsException();
			}
			hotelPrice = qProperty.HousePrice();
			if (hotelPrice == 0.00) {
				throw new SiteCantImproveException();
			}
			if (qPlayer.HasFunds( hotelPrice)) {
				fNumHotels--;
				qProperty.BuildHotel();
				qPlayer.AddFunds( -hotelPrice);
				AddFunds( hotelPrice);
				ret = true;
			}
			return ret;
		}
		
		/**
		 * Sells the specified property to the specified player.
		 * The player's fund will be debited, and the bank credited
		 *
		 * Preconditions:
		 *    The bank must own the property
		 *    The player must have adequate funds
		 *
		 * @param  qProperty  the property being improved
		 * @param  qPlayer  the player buying the house
		 */
		public boolean SellProperty( CProperty qProperty, CPlayer qPlayer) throws PropertyNotFoundException, NotEnoughCashException {
			boolean ret = false;
			double thePrice;
			
			if (fTrace) {
				System.out.println( "CBank.SellProperty( " + qProperty.Name() + ", " + qPlayer.Name() + ")");
			}
			// does bank own Property?
			if (!OwnsProperty( qProperty)) {
				throw new PropertyNotFoundException();
			}
			thePrice = qProperty.Price();
			
			// does player have enough money?
			if (qPlayer.HasFunds( thePrice)) {
				if (!fProperties.remove( qProperty)) {
					throw new PropertyNotFoundException();
				}
				qProperty.Buy( qPlayer);
				qPlayer.AddProperty( qProperty);
				qPlayer.AddFunds( -thePrice);
				AddFunds( thePrice);
				ret = true;
			}
			return ret;
		}
	}
	
	// ============================================================================
	
	
	public class CDie {
		private Random fRandom;
		private int fCount[] = { 0, 0, 0, 0, 0, 0, 0};
		
		CDie() {
			if (fTrace) {
				System.out.println( "CDie.CDie()");
			}
			fRandom = new Random();
		}
		
		public int Roll() {
			int ret = 0;
			
			if (fTrace) {
				System.out.println( "CDie.Roll()");
			}
			ret = Math.abs( fRandom.nextInt() % 6) + 1;
			fCount[ret]++;
			return ret;
		}
	}
	
	// ============================================================================
	
	
	public class CDice {
		private CDie fDie1;
		private CDie fDie2;
		private boolean fDoubles;
		private int fRoll;
		
		CDice() {
			if (fTrace) {
				System.out.println( "CDice.CDice()");
			}
			fDie1 = new CDie();
			fDie2 = new CDie();
		}
		
		public void Roll() {
			int val1, val2;
			
			if (fTrace) {
				System.out.println( "CDice.Roll()");
			}
			val1 = fDie1.Roll();
			val2 = fDie2.Roll();
			if (val1 == val2) {
				fDoubles = true;
			} else {
				fDoubles = false;
			}
			fRoll = val1 + val2;
		}
		
		public boolean IsDoubles() {
			if (fTrace) {
				System.out.println( "CDice.IsDoubles()");
			}
			return fDoubles;
		}
		
		public int Total() {
			if (fTrace) {
				System.out.println( "CDice.Total()");
			}
			return fRoll;
		}
	}
	
	// ============================================================================
	
	
	public class CGame {
		private CBoard fBoard;
		private Vector fPlayers;
		private CDice fDice;
		private CCommunityChestDeck fCommunityChest;
		private CChanceDeck fChance;
		private CBank fBank;
		private boolean fGameInProgress;
		
		CGame( double qFloat) {
			if (fTrace) {
				System.out.print( "CGame.CGame( ");
				System.out.print( qFloat);
				System.out.println( ")");
			}
			fBank = new CBank( qFloat);
			fBoard = new CBoard( Country.USA, fBank);
			fBank.SetBoard( fBoard);
			fDice = new CDice();
			fCommunityChest = new CCommunityChestDeck();
			fChance = new CChanceDeck();
			fPlayers = new Vector();
			fGameInProgress = true;
		}
		
		public CBank Bank() {
			if (fTrace) {
				System.out.println( "CGame.Bank()");
			}
			return fBank;
		}
		
		public void AddPlayer( CPlayer qPlayer) {
			if (fTrace) {
				System.out.println( "CGame.AddPlayer( " + qPlayer.Name() + ")");
			}
			qPlayer.SetBoard( fBoard);
			qPlayer.SetBank( fBank);
			qPlayer.SetDice( fDice);
			fPlayers.add( qPlayer);
		}
		
		public void Play() throws BankBrokeException, NotEnoughCashException, PlayerNotFoundException, PropertyNotFoundException, TooManyHousesException {
			CPlayer thisPlayer;
			CPlayer theWinner = null;
			int numBankrupts;
			int turnNo = 0;
			
			if (fTrace) {
				System.out.println( "-----------------------------------------------------------------------------------");
				System.out.println( "CGame.Play()");
			}
			while (fGameInProgress) {
				numBankrupts = 0;
				if (fVerbose) {
					System.out.print( "turn ");
					System.out.println( turnNo);
				}
				if (fPlayers.isEmpty()) {
					System.out.println( "No players!");
					throw new PlayerNotFoundException();
				}
				for (Iterator it = fPlayers.iterator(); it.hasNext();) {
					thisPlayer = (CPlayer) it.next();
					System.out.println( "Player turn " + thisPlayer.Name());
					thisPlayer.TakeTurn( turnNo);
					if (thisPlayer.IsBankrupt()) {
						numBankrupts++;
					} else {
						theWinner = thisPlayer;
					}
				}
				if (numBankrupts == (fPlayers.size() - 1)) {
					if (theWinner == null) {
						throw new PlayerNotFoundException();
					}
					// we have one player left - the winner!
					fGameInProgress = false;
					if (fVerbose) {
						System.out.println( "winner: " + theWinner.Name());
					}
				}
				
				fBank.DoStatement( "After");
				// at the end of each round, some assertions
				// +=+ the total amount of cash in the system must be constant
				// +=+ the total number of cards must be constant
				// +=+ the total number of properties must be constant
				turnNo++;
			}
			if (fVerbose) {
				System.out.println( "CGame.Play end");
			}
			throw new PlayerNotFoundException();
		}
	}
	
	// ============================================================================
	
	
	public class CSession {
		int fNumPlayers;
		int fNumGames;
		Random fRandom;
		CGame fGame;
		
		CSession( int qNumGames) {
			if (fTrace) {
				System.out.println( "CSession.CSession()");
			}
			fNumGames = qNumGames;
		}
		
		public void run() {
			int gameNo;
			
			if (fTrace) {
				System.out.println( "CSession.run()");
			}
			if (fVerbose) {
				System.out.print( "fNumGames=");
				System.out.println( fNumGames);
			}
			for ( gameNo = 0; gameNo < fNumGames; gameNo++) {
				try {
					DoGame();
				} catch (BankBrokeException e) {
					System.out.println( "bank broke");
				} catch (NotEnoughCashException e) {
					System.out.println( "not enough cash");
				} catch (PlayerNotFoundException e) {
					System.out.println( "winner not found");
				} catch (PropertyNotFoundException e) {
					System.out.println( "property not found");
				} catch (TooManyHousesException e) {
					System.out.println( "too many houses");
				}
			}
		}
		
		public void DoGame() throws BankBrokeException, NotEnoughCashException, PlayerNotFoundException, PropertyNotFoundException, TooManyHousesException {
			int playerNo;
			CPlayer newPlayer;
			
			if (fTrace) {
				System.out.println( "CSession.DoGame()");
			}
			PlayerName.Reset();
			Piece.Reset();
			fRandom = new Random();
			fNumPlayers = fRandom.nextInt( kMaxPlayers - kMinPlayers + 1) + kMinPlayers;
			fNumPlayers = Math.min( fNumPlayers, PlayerName.size());
			fNumPlayers = Math.min( fNumPlayers, Piece.size());
			if (fVerbose) {
				System.out.print( "CSession.DoGame - fNumPlayers=");
				System.out.println( fNumPlayers);
			}
			fGame = new CGame( kDefaultBankFloat);
			if (fNumPlayers > 0) {
				for ( playerNo = 0; playerNo < fNumPlayers; playerNo++)	{
					PlayerName playerName = PlayerName.UniqueRandom();
					Piece playerPiece = Piece.UniqueRandom();
					
					if (fVerbose) {
						System.out.println( "adding a player");
					}
					
					newPlayer = new CPlayer( playerName.Name(), kDefaultPlayerFloat, playerPiece, 0);
					fGame.AddPlayer( newPlayer);
					if (fVerbose) {
						System.out.println( "player added: " + newPlayer.Name());
					}
				}
				fGame.Play();
			} else {
				System.out.println( "why no players you shit?");
				System.out.print( "fNumPlayers=");
				System.out.println( fNumPlayers);
			}
		}
	}

	BoardGameSimulator() {
		CSession theSession;
		
		theSession = new CSession( kNumGames);
		theSession.run();
	}
	
    public static void main (String args[]) {
		BoardGameSimulator theBGS;
		int i;
		
        // insert code here...
        System.out.println("Hello World!");
		theBGS = new BoardGameSimulator();
    }
}
