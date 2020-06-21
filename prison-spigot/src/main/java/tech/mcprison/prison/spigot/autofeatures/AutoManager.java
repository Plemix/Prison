package tech.mcprison.prison.spigot.autofeatures;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;

import com.vk2gpz.tokenenchant.event.TEBlockExplodeEvent;

import tech.mcprison.prison.mines.data.Mine;
import tech.mcprison.prison.spigot.SpigotPrison;
import tech.mcprison.prison.spigot.spiget.BluesSpigetSemVerComparator;


/**
 * @author GABRYCA
 * @author RoyalBlueRanger
 */
public class AutoManager 
	extends AutoManagerFeatures
	implements Listener {
	
	public AutoManager() {
        super();
        
        // Save this instance within the SpigotPrison instance so it can be accessed
        // from non-event listeners:
        SpigotPrison.getInstance().setAutoFeatures( this );
    }


//    /**
//     * <p>Had to set to a EventPriorty.LOW so other plugins can work with the blocks.
//     * The other plugins were EZBlock & SellAll. This function was canceling the
//     * event after it auto picked it up, so the other plugins were not registering
//     * the blocks as being broken.
//     * </p>
//     * 
//     * @param e
//     */
//    @EventHandler(priority=EventPriority.LOW) 
//    public void onBlockBreak(BlockBreakEvent e) {
//    	
//    	if ( !e.isCancelled() && e.getBlock().getType() != null) {
//    		
//    		// Get the player objects: Spigot and the Prison player:
//    		Player p = e.getPlayer();
//    		// SpigotPlayer player = new SpigotPlayer( p );
//    		
//    		// Validate that the event is happening within a mine since the
//    		// onBlockBreak events here are only valid within the mines:
//    		Optional<Module> mmOptional = Prison.get().getModuleManager().getModule( PrisonMines.MODULE_NAME );
//    		if ( mmOptional.isPresent() && mmOptional.get().isEnabled() ) {
//    			PrisonMines mineManager = (PrisonMines) mmOptional.get();
//    			
//    			for ( Mine mine : mineManager.getMines() ) {
//    				SpigotBlock block = new SpigotBlock(e.getBlock());
//    				if ( mine.isInMine( block.getLocation() ) ) {
//    					
//    					applyAutoEvents( e, mine, p );
//    					break;
//    				}
//    			}
//    		}
//    	}
//    }

    /**
     * <p>The optimized logic on how an BlockBreakEvent is handled is within the OnBlockBreakEventListener
     * class and optimizes mine reuse.
     * </p>
     *
     * <p>Had to set to a EventPriorty.LOW so other plugins can work with the blocks.
     * The other plugins were EZBlock & SellAll. This function was canceling the
     * event after it auto picked it up, so the other plugins were not registering
     * the blocks as being broken.
     * </p>
     * 
     * 
     */
    @Override
    @EventHandler(priority=EventPriority.LOW) 
    public void onBlockBreak(BlockBreakEvent e) {
    	super.onBlockBreak(e);
    }
    
    @Override
    @EventHandler(priority=EventPriority.LOW) 
    public void onTEBlockExplode(TEBlockExplodeEvent e) {
    	super.onTEBlockExplode(e);
    }
    
    
    
    @Override
	public void doAction( Mine mine, BlockBreakEvent e ) {
    	applyAutoEvents( e, mine );
	}
    
    
    @Override
    public void doAction( Mine mine, TEBlockExplodeEvent e, int blockCount ) {
    	applyAutoEvents( e, mine, blockCount );
    }
    
    // Prevents players from picking up armorStands (used for holograms), only if they're invisible
	@EventHandler
	public void manipulate(PlayerArmorStandManipulateEvent e) {
		if(!e.getRightClicked().isVisible()) {
			e.setCancelled(true);
		}
	}

	boolean isOnePointFourPointFour(){
		return new BluesSpigetSemVerComparator().compareMCVersionTo("1.14.4") < 0;
	}

	private void applyAutoEvents( BlockBreakEvent e, Mine mine ) {
		
		if ( isAutoManagerEnabled() && !e.isCancelled() ) {
			
			Player p = e.getPlayer();
			
			double lorePickup = doesItemHaveAutoFeatureLore( ItemLoreEnablers.Pickup, p );
			double loreSmelt = doesItemHaveAutoFeatureLore( ItemLoreEnablers.Smelt, p );
			double loreBlock = doesItemHaveAutoFeatureLore( ItemLoreEnablers.Block, p );
			
			boolean permPickup = p.hasPermission( "prison.autofeatures.pickup" ) ||
					lorePickup == 100.0 ||
					lorePickup > 0 && lorePickup <= getRandom().nextDouble() * 100;
			boolean permSmelt = p.hasPermission( "prison.autofeatures.smelt" ) ||
					loreSmelt == 100.0 ||
					loreSmelt > 0 && loreSmelt <= getRandom().nextDouble() * 100;
			boolean permBlock = p.hasPermission( "prison.autofeatures.block" ) ||
					loreBlock == 100.0 ||
					loreBlock > 0 && loreBlock <= getRandom().nextDouble() * 100;

			//
					
			
			// AutoPickup
			if ( permPickup || isAutoPickupEnabled()) {
				
				autoFeaturePickup(e, p);
			}
			
			
			// AutoSmelt
			if ( permSmelt ||  isAutoSmeltEnabled()){
				
				autoFeatureSmelt( e, p );
			}
			
			// AutoBlock
			if ( permBlock || isAutoBlockEnabled()) {
				
				autoFeatureBlock( e, p );
			}
			
					
			if ( e.isCancelled() && ( permPickup || permSmelt || permBlock )) {
				// The event was canceled, so the block was successfully broke, so increment the name counter:
				
				ItemStack itemInHand = SpigotPrison.getInstance().getCompatibility().getItemInMainHand( p );
				
				itemLoreCounter( itemInHand, ItemLoreCounters.itemLoreBlockBreakCount, 1 );
			}
		}

	}


	/**
	 * <p>This function gets called for EACH block that is impacted by the
	 * explosion event.  The event may have have a list of blocks, but not all
	 * blocks may be included in the mine. This function is called ONLY when
	 * a block is within a mine,
	 * </p>
	 * 
	 * <p>The event TEBlockExplodeEvent has already taken place and it handles all
	 * actions such as auto pickup, auto smelt, and auto block.  The only thing we
	 * need to do is to record the number of blocks that were removed during 
	 * the explosion event. The blocks should have been replaced by air.
	 * </p>
	 * 
	 * <p>Normally, prison based auto features, and or the perms need to be enabled 
	 * for the auto features to be enabled, but since this is originating from another
	 * plugin and another external configuration, we cannot test for any real perms
	 * or configs.  The fact that this event happens within one of the mines is 
	 * good enough, and must be counted.
	 * </p>
	 * 
	 * @param e
	 * @param mine
	 */
	private void applyAutoEvents( TEBlockExplodeEvent e, Mine mine, int blockCount ) {
		
		Player p = e.getPlayer();
		
//		double lorePickup = doesItemHaveAutoFeatureLore( ItemLoreEnablers.Pickup, p );
//		double loreSmelt = doesItemHaveAutoFeatureLore( ItemLoreEnablers.Smelt, p );
//		double loreBlock = doesItemHaveAutoFeatureLore( ItemLoreEnablers.Block, p );
//		
//		boolean permPickup = p.hasPermission( "prison.autofeatures.pickup" ) ||
//				lorePickup == 100.0 ||
//				lorePickup > 0 && lorePickup <= getRandom().nextDouble() * 100;
//		boolean permSmelt = p.hasPermission( "prison.autofeatures.smelt" ) ||
//				loreSmelt == 100.0 ||
//				loreSmelt > 0 && loreSmelt <= getRandom().nextDouble() * 100;
//		boolean permBlock = p.hasPermission( "prison.autofeatures.block" ) ||
//				loreBlock == 100.0 ||
//				loreBlock > 0 && loreBlock <= getRandom().nextDouble() * 100;

//		if ( permPickup || permSmelt || permBlock ||
//				isAreEnabledFeatures()) 
		{
			
			
			ItemStack itemInHand = SpigotPrison.getInstance().getCompatibility().getItemInMainHand( p );
			
			
			if ( e.isCancelled() ) {
				// The event was canceled, so the block was successfully broke, so increment the name counter:
				
			itemLoreCounter( itemInHand, ItemLoreCounters.itemLoreBlockExplodeCount, blockCount );
			}
		}			
	}

}
