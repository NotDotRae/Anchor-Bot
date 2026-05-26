import { v } from "convex/values";
import { internalMutation, internalQuery } from "./_generated/server";

const keys: Record<string, string> = {
  sticky: "stickies",
  slowSticky: "slowStickies",
  embedSticky: "embedStickies",
  embedImage: "embedImages",
  bigEmbedImage: "bigEmbedImages",
  disabled: "disabled",
  webhookUrl: "webhookUrls",
  webhookMessage: "webhookMessages",
  prefix: "prefixes",
};

export const state = internalQuery({
  handler: async (ctx) => {
    const result: Record<string, Record<string, string>> = {
      stickies: {},
      slowStickies: {},
      embedStickies: {},
      embedImages: {},
      bigEmbedImages: {},
      disabled: {},
      webhookUrls: {},
      webhookMessages: {},
      prefixes: {},
    };

    const records = await ctx.db.query("records").collect();
    for (const record of records) {
      const key = keys[record.kind];
      if (key) {
        result[key][record.id] = record.value;
      }
    }
    return result;
  },
});

export const upsert = internalMutation({
  args: {
    kind: v.string(),
    id: v.string(),
    value: v.string(),
  },
  handler: async (ctx, args) => {
    const existing = await ctx.db
      .query("records")
      .withIndex("by_kind_id", (q) => q.eq("kind", args.kind).eq("id", args.id))
      .unique();

    const row = {
      kind: args.kind,
      id: args.id,
      value: args.value,
      updatedAt: Date.now(),
    };

    if (existing) {
      await ctx.db.patch(existing._id, row);
    } else {
      await ctx.db.insert("records", row);
    }
  },
});

export const remove = internalMutation({
  args: {
    kind: v.string(),
    id: v.string(),
  },
  handler: async (ctx, args) => {
    const existing = await ctx.db
      .query("records")
      .withIndex("by_kind_id", (q) => q.eq("kind", args.kind).eq("id", args.id))
      .unique();

    if (existing) {
      await ctx.db.delete(existing._id);
    }
  },
});

export const removeChannel = internalMutation({
  args: {
    channelId: v.string(),
  },
  handler: async (ctx, args) => {
    const records = await ctx.db.query("records").collect();
    for (const record of records) {
      if (record.id === args.channelId && record.kind !== "prefix") {
        await ctx.db.delete(record._id);
      }
    }
  },
});
