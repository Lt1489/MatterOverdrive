/*
 * This file is part of Matter Overdrive
 * Copyright (c) 2015., Simeon Radivoev, All rights reserved.
 *
 * Matter Overdrive is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Matter Overdrive is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Matter Overdrive.  If not, see <http://www.gnu.org/licenses>.
 */

package matteroverdrive.network.packet.server;

import io.netty.buffer.ByteBuf;
import matteroverdrive.MatterOverdrive;
import matteroverdrive.api.android.IBioticStat;
import matteroverdrive.entity.android_player.AndroidPlayer;
import matteroverdrive.entity.player.MOPlayerCapabilityProvider;
import matteroverdrive.network.packet.PacketAbstract;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.EnumSet;

/**
 * Created by Simeon on 7/9/2015.
 */
public class PacketAndroidChangeAbility extends PacketAbstract
{
	String ability;

	public PacketAndroidChangeAbility()
	{

	}

	public PacketAndroidChangeAbility(String ability)
	{
		this.ability = ability;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		ability = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, ability);
	}

	public static class ServerHandler extends AbstractServerPacketHandler<PacketAndroidChangeAbility>
	{

		@Override
		public void handleServerMessage(EntityPlayerMP player, PacketAndroidChangeAbility message, MessageContext ctx)
		{
			IBioticStat stat = MatterOverdrive.statRegistry.getStat(message.ability);
			if (stat != null)
			{
				AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(player);
				if (androidPlayer.isUnlocked(stat, 0) && stat.showOnWheel(androidPlayer, androidPlayer.getUnlockedLevel(stat)))
				{
					androidPlayer.setActiveStat(stat);
					androidPlayer.sync(EnumSet.of(AndroidPlayer.DataType.STATS));
				}
			}
		}
	}
}
